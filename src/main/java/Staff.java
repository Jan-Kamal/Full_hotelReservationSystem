import java.time.LocalDate;
 

public abstract class Staff {
 
   
    public enum Role {
        ADMIN, RECEPTIONIST
    }
 
 
    private String username;
    private String password;
    private LocalDate dateOfBirth;
    private Role role;
    private int workingHours;
 
    public Staff(String username, String password, LocalDate dateOfBirth,
                 Role role, int workingHours) {
        this.username = username;
        this.password = validatePassword(password);
        this.dateOfBirth = dateOfBirth;
        this.role = role;
        this.workingHours = workingHours;
    }
 

    private String validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        return password;
    }
 
 
    
 
    public boolean login(String username, String password) {
        if (this.username.equals(username) && PasswordUtils.verify(password, this.password)) {
            System.out.println("Staff login successful. Welcome, " + this.username + " (" + role + ")!");
            return true;
        }
        System.out.println("Invalid credentials.");
        return false;
    }

    /** Returns the stored password value — used only by the persistence layer. */
    public String getPasswordForStorage() { return password; }
 
    public abstract void performDuties();
 

    public String getUsername()              { return username; }
    public void setUsername(String u)        { this.username = u; }
 
    public LocalDate getDateOfBirth()        { return dateOfBirth; }
    public void setDateOfBirth(LocalDate d)  { this.dateOfBirth = d; }
 
    public Role getRole()                    { return role; }
    public void setRole(Role r)              { this.role = r; }
 
    public int getWorkingHours()             { return workingHours; }
    public void setWorkingHours(int h)       { this.workingHours = h; }
 
    public void setPassword(String p)        { this.password = validatePassword(p); }
 
    @Override
    public String toString() {
        return "Staff{username='" + username + "', role=" + role +
               ", workingHours=" + workingHours + ", dateOfBirth=" + dateOfBirth + "}";
    }
}