package be.ordina.junit5.demo;

import org.junit.jupiter.api.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class SimpleTest {
    private HelloSayer helloSayer;

    @BeforeAll
    static void beforeAll(){

    }
    @BeforeEach
    void setUp() {
        helloSayer = new HelloSayer();
    }
    @Test
    void helloWorld() {
        assertThat(helloSayer.hello("world"),is("Hello, world!"));
    }
    @Test
    void helloDude(){
        assertThat(helloSayer.hello("dude"), is("Hello, dude!"));
    }
    @Test
    void helloNull(TestReporter testReporter){
        testReporter.publishEntry("derp");
        assertThrows(IllegalArgumentException.class,
                ()->helloSayer.hello(null),"str is Mandatory");
    }
    @Test
    void helloNull2(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> helloSayer.hello(null));
        assertThat(exception.getMessage(), is("str is Mandatory"));
    }
    @AfterEach
    void afterEach() {

    }
    @AfterAll
    static void afterAll() {

    }
}
