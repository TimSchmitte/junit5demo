package be.ordina.junit5.demo.extensions.custom;

import org.h2.tools.RunScript;
import org.h2.tools.Script;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Stream;

public class TempDBExtension implements ParameterResolver, AfterEachCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(TempDBExtension.class);

    public static final String DB_INSTANCE_KEY = "dbInstance";
    private static final String DB_BACKUP_FILE = "dbBackupFile";
    private String initScript;

    private TempDBExtension() {
        this.initScript = null;
    }

    public TempDBExtension(String initScript) {
        this.initScript = initScript;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface TempDB {

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.isAnnotated(TempDB.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        if (!parameterContext.getParameter().getType().isAssignableFrom(EmbeddedDatabase.class)) {
            throw new ExtensionConfigurationException("Parameter annotated with @TempDB should be assignable from EmbeddedDatabase");
        }

        EmbeddedDatabase embeddedDatabase = getOrCreateDb(extensionContext);
        createAndStoreBackupFileIfNotPresent(extensionContext, embeddedDatabase);
        return embeddedDatabase;
    }


    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if(!appliesToMethod(context)){
            return;
        }
        LOGGER.info("execution context hashcode" + context.hashCode());
        EmbeddedDatabase embeddedDatabase = getStore(context.getRoot()).get(DB_INSTANCE_KEY, EmbeddedDatabase.class);
        if (embeddedDatabase != null) {
            RunScript.execute(embeddedDatabase.getConnection(), new FileReader(getStore(context).get(DB_BACKUP_FILE, File.class)));
        }
    }

    private File createAndStoreBackupFileIfNotPresent(ExtensionContext extensionContext, EmbeddedDatabase embeddedDatabase) {
        return getStore(extensionContext).getOrComputeIfAbsent(DB_BACKUP_FILE, (__)->generateBackupFile(embeddedDatabase), File.class);
    }


    private boolean appliesToMethod(ExtensionContext context) {
        return Stream.of(context.getTestMethod().get().getParameters()).anyMatch(parameter -> parameter.isAnnotationPresent(TempDB.class));
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create("TempDBExtensionWithInitScript:" + initScript));
    }

    private EmbeddedDatabase getOrCreateDb(ExtensionContext context) {
        return getStore(context.getRoot()).getOrComputeIfAbsent(DB_INSTANCE_KEY, s -> createDb(), EmbeddedDatabase.class);
    }

    private EmbeddedDatabase createDb() {
        LOGGER.info("creating new db");
        EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2);
        if (initScript != null) {
            embeddedDatabaseBuilder.addScript(initScript);
        }
        return embeddedDatabaseBuilder.build();
    }


    private static File generateBackupFile(final DataSource dataSource)  {
        try (final Connection connection = dataSource.getConnection()) {
            LOGGER.info("preparing H2 baseline");
            File dbBackup = File.createTempFile("backup", ".sql");
            Script.process(connection, dbBackup.getAbsolutePath(), "DROP", "");
            LOGGER.info("preparing H2 baseline done");
            return dbBackup;
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
