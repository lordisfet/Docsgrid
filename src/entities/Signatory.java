package entities;

import entities.User.Employee;
import exceptions.SignatoryValidationException;

public class Signatory {
    private final Employee employee;
    private boolean signStatus;

    public Signatory(Employee employee, boolean signStatus) throws SignatoryValidationException {
        if (employee == null) {
            throw new SignatoryValidationException("Employee cannot be null");
        }

        this.employee = employee;
        this.signStatus = signStatus;
    }

    public Employee getEmployee() {
        return employee;
    }

    public boolean isSigned() {
        return signStatus;
    }

    public void setSignStatus(boolean signStatus) {
        this.signStatus = signStatus;
    }

    @Override
    public String toString() {
        return "Signatory{" +
                "employee=" + employee +
                ", signStatus=" + signStatus +
                '}';
    }
}
