import java.util.*;

// User class to store user details
class User {
    private String name;
    private String email;
    private List<String> mobileNumbers;

    public User(String name, String email, List<String> mobileNumbers) {
        this.name = name;
        this.email = email;
        this.mobileNumbers = new ArrayList<>(mobileNumbers);
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<String> getMobileNumbers() { return mobileNumbers; }

    public void setName(String name) { this.name = name; }
    public void setMobileNumbers(List<String> mobileNumbers) {
        this.mobileNumbers = new ArrayList<>(mobileNumbers);
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Email: " + email + ", Numbers: " + mobileNumbers;
    }
}

// Custom exception for user not found
class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}

// UserService to handle CRUD operations
class UserService {
    private final Map<String, User> userMap = new HashMap<>();

    // Create a new user
    public void createUser(String name, String email, List<String> numbers) {
        if (userMap.containsKey(email)) {
            System.out.println("User already exists with the email: " + email);
            return;
        }
        userMap.put(email, new User(name, email, numbers));
        System.out.println("User created successfully.");
    }

    // Update existing user
    public void updateUser(String email, String newName, List<String> newNumbers) throws UserNotFoundException {
        User user = findUser(email);
        user.setName(newName);
        user.setMobileNumbers(newNumbers);
        System.out.println("User updated.");
    }

    // Delete user
    public void deleteUser(String email) throws UserNotFoundException {
        if (!userMap.containsKey(email)) {
            throw new UserNotFoundException("User not found with email: " + email);
        }
        userMap.remove(email);
        System.out.println("User deleted.");
    }

    // Find a user by email
    public User findUser(String email) throws UserNotFoundException {
        User user = userMap.get(email);
        if (user == null) throw new UserNotFoundException("User not found with email: " + email);
        return user;
    }

    // Find users by name (partial match)
    public List<User> findUsersByName(String name) {
        List<User> results = new ArrayList<>();
        for (User user : userMap.values()) {
            if (user.getName().toLowerCase().contains(name.toLowerCase())) {
                results.add(user);
            }
        }
        return results;
    }

    // Get all users
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    // Validate email format
    public boolean isValidEmail(String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$");
    }

    // Validate mobile number (10 digits)
    public boolean isValidMobileNumber(String number) {
        return number.matches("\\d{10}");
    }
}

// Main class
public class ConsoleApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserService service = new UserService();

        while (true) {
            System.out.println("\n1. Create User\n2. Update User\n3. Delete User\n4. Get Single User");
            System.out.println("5. Get All Users\n6. Exit\n7. Search User by Name");
            System.out.print("Choose: ");
            String input = sc.nextLine();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Please enter a number.");
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Enter name: ");
                        String name = sc.nextLine();

                        String email;
                        while (true) {
                            System.out.print("Enter email: ");
                            email = sc.nextLine();
                            if (service.isValidEmail(email)) break;
                            System.out.println("Invalid email format. Please try again.");
                        }

                        List<String> numbers = readValidNumbers(sc, service);
                        service.createUser(name, email, numbers);
                        break;

                    case 2:
                        String updateEmail;
                        while (true) {
                            System.out.print("Enter email of user to update: ");
                            updateEmail = sc.nextLine();
                            if (service.isValidEmail(updateEmail)) break;
                            System.out.println("Invalid email format. Please try again.");
                        }

                        System.out.print("Enter new name: ");
                        String newName = sc.nextLine();

                        List<String> newNumbers = readValidNumbers(sc, service);
                        service.updateUser(updateEmail, newName, newNumbers);
                        break;

                    case 3:
                        String deleteEmail;
                        while (true) {
                            System.out.print("Enter email to delete: ");
                            deleteEmail = sc.nextLine();
                            if (service.isValidEmail(deleteEmail)) break;
                            System.out.println("Invalid email format. Please try again.");
                        }
                        service.deleteUser(deleteEmail);
                        break;

                    case 4:
                        String searchEmail;
                        while (true) {
                            System.out.print("Enter email to search: ");
                            searchEmail = sc.nextLine();
                            if (service.isValidEmail(searchEmail)) break;
                            System.out.println("Invalid email format. Please try again.");
                        }
                        User found = service.findUser(searchEmail);
                        System.out.println("User found: " + found);
                        break;

                    case 5:
                        List<User> allUsers = service.getAllUsers();
                        if (allUsers.isEmpty()) {
                            System.out.println("No users found.");
                        } else {
                            for (User user : allUsers) {
                                System.out.println(user);
                            }
                        }
                        break;

                    case 6:
                        System.out.println("Exiting the program. Goodbye!");
                        return;

                    case 7:
                        System.out.print("Enter name to search: ");
                        String nameToSearch = sc.nextLine();
                        List<User> matchedUsers = service.findUsersByName(nameToSearch);
                        if (matchedUsers.isEmpty()) {
                            System.out.println("No users found with name: " + nameToSearch);
                        } else {
                            for (User u : matchedUsers) {
                                System.out.println(u);
                            }
                        }
                        break;

                    default:
                        System.out.println("Invalid choice. Please select from the menu.");
                }
            } catch (UserNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // Read and validate list of mobile numbers
    private static List<String> readValidNumbers(Scanner sc, UserService service) {
        int count;
        while (true) {
            System.out.print("Enter how many mobile numbers: ");
            String countInput = sc.nextLine();
            try {
                count = Integer.parseInt(countInput);
                if (count <= 0 || count > 10) {
                    System.out.println("Enter a positive number less than or equal to 10.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid integer.");
            }
        }

        List<String> numbers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            while (true) {
                System.out.print("Enter mobile number " + (i + 1) + ": ");
                String number = sc.nextLine();
                if (service.isValidMobileNumber(number)) {
                    numbers.add(number);
                    break;
                } else {
                    System.out.println("Invalid mobile number. Must be 10 digits.");
                }
            }
        }
        return numbers;
    }
}
