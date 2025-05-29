import annotations.*;

public class FirstTestClass {
    @BeforeSuite
    public static void setUp() {
        System.out.println("Setting up...");
    }

    @Test(priority = 10)
    public void test1() throws NoSuchMethodException {
        var priority = FirstTestClass.class.getDeclaredMethod("test1").getAnnotation(Test.class).priority();
        System.out.printf("This is test method 1 with priority %d\n", priority);
    }

    /*@Test(priority = 20)
    public void test2() {
        System.out.println("This is test method 2...");
    }*/

    @Test(priority = 3)
    public void test3() throws NoSuchMethodException {
        var priority = FirstTestClass.class.getDeclaredMethod("test3").getAnnotation(Test.class).priority();
        System.out.printf("This is test method 3 with priority %s\n", priority);
    }

    /*@Test(priority = 0)
    public void test4() {
        System.out.println("This is test method 4...");
    }*/

    /*@Test(priority = -1)
    public void test5() {
        System.out.println("This is test method 5...");
    }*/

    @Test(priority = 1)
    public void test6() throws NoSuchMethodException {
        var priority = FirstTestClass.class.getDeclaredMethod("test6").getAnnotation(Test.class).priority();
        System.out.printf("This is test method 6 with priority %d\n", priority);
    }

    /*@BeforeSuite
    public void beforeSuite() {}*/

    /*@AfterSuite
    public void afterSuite() {}*/

    @AfterSuite
    public static void tearDown() {
        System.out.println("Tearing down...");
    }

    @Test
    public void test7() throws NoSuchMethodException {
        var priority = FirstTestClass.class.getDeclaredMethod("test7").getAnnotation(Test.class).priority();
        System.out.printf("This is test method 7 with priority %s\n", priority);
    }

    @Test(priority = 6)
    @CsvSource(value = "10, Java, 20, true")
    public void test8(int a, String b, int c, boolean d) throws NoSuchMethodException {
        var priority = FirstTestClass.class
                .getDeclaredMethod("test8", int.class, String.class, int.class, boolean.class)
                .getAnnotation(Test.class).priority();
        System.out.printf("This is test method 8 with priority %s\n", priority);
        System.out.printf("%d, %s, %d, %b\n", a, b, c, d);
    }

    @BeforeTest
    public void beforeTest() {
        System.out.println("Preparing tests...");
    }

    @AfterTest
    public void afterTest() {
        System.out.println("Stopping tests...");
    }
}
