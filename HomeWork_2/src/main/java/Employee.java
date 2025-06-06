public class Employee {
    private final String name;
    private final Integer age;
    private final String position;

    public Employee(EmployeeBuilder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.position = builder.position;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public String getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", position='" + position + '\'' +
                '}';
    }

    public static class EmployeeBuilder {
        private String name;
        private Integer age;
        private String position;

        public EmployeeBuilder name(String name)
        {   this.name = name;
            return this;
        }

        public EmployeeBuilder age(Integer age) {
            this.age = age;
            return this;
        }

        public EmployeeBuilder position(String position) {
            this.position = position;
            return this;
        }

        public Employee build() {
            return new Employee(this);
        }
    }
}
