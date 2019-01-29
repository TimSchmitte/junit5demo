package be.ordina.junit5.demo.testselection;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assumptions.assumeTrue;


class SelectiveTests {
    @Tag("fastTest")
    @Test
    void fastTest(){
        Assertions.assertTrue(true,"exceptionally useful test");
    }

    @Tag("slowTest")
    @Test
    void slowTest() throws InterruptedException {
        Thread.sleep(2000L);
    }

    @Disabled
    @Test
    void disabledTest(){

    }

    @DisabledOnOs(OS.MAC)
    @Test
    void disabledOnMac(){

    }


    @EnabledOnOs(OS.MAC)
    @Test
    void enabledOnMac(){

    }
    @Test
    void assumingTest(){
        assumeTrue(LocalDate.now().isAfter(LocalDate.of(2018, 9 , 26)));
    }
}
