package entities;

import entities.user.Employee;
import exceptions.SignatoryValidationException;

/**
 * Represents a signatory for a document, associating an employee and their signing status.
 */
public class Signatory {
    private final Employee employee;
    private boolean signStatus;

    /**
     * Constructs a Signatory with the given employee and sign status.
     *
     * @param employee   the employee who must sign; must not be null
     * @param signStatus initial signing status (true if already signed)
     * @throws SignatoryValidationException if employee is null
     */
    public Signatory(Employee employee, boolean signStatus) throws SignatoryValidationException {
        if (employee == null) {
            throw new SignatoryValidationException("Employee cannot be null");
        }

        this.employee = employee;
        this.signStatus = signStatus;
    }

    /**
     * Returns the associated employee.
     *
     * @return employee who signs
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Checks if the signatory has signed.
     *
     * @return true if signed, false otherwise
     */
    public boolean isSigned() {
        return signStatus;
    }

    /**
     * Updates the sign status.
     *
     * @param signStatus new signing status
     */
    public void setSignStatus(boolean signStatus) {
        this.signStatus = signStatus;
    }

    /**
     * Returns a string representation of the Signatory.
     *
     * @return string including employee and sign status
     */
    @Override
    public String toString() {
        return "Signatory{" +
                "employee=" + employee +
                ", signStatus=" + signStatus +
                '}';
    }
}
