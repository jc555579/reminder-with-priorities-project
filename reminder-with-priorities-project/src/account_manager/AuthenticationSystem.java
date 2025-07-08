package account_manager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationSystem {
	private String usersPath;

	// username and hashed password of users
	Map<String, String> users = new HashMap<>();

	// The file should be csv
	public AuthenticationSystem() {
		this.usersPath = "src/resources/user-accounts.txt";

		try {
			loadUsers();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public AuthenticationSystem(String userPath) {
		this.usersPath = userPath;

		try {
			loadUsers();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public boolean register(String email, String password) {
		// Successfull registration
		if (!users.containsKey(email)) {
			try {
				// Making the password hashed
				String hashedPassword = getHashedPassword(password);

				// Saving files for users
				saveUsers(email, hashedPassword);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Unsuccessfull registration, it means that the email is not unique
		return false;
	}

	// TO DO: CONTINUE
	public boolean login(String email, String password) {
		String hashedPassword = getHashedPassword(password);

		return hashedPassword.equals(users.get(email));
	}

	private void loadUsers() throws IOException, Exception {
		try (BufferedReader br = new BufferedReader(new FileReader(usersPath))) {
			String line;
			while ((line = br.readLine()) != null) {
				// parts[0] is username and parts[1] is password
				String parts[] = line.trim().split(", ");

				// for double checking
				if (parts.length == 2) {
					users.put(parts[0], parts[1]);
				} else {
					throw new IOException(
							"Check users file if it's csv, and it must contain only username and hashed password!");
				}
			}

		}
	}

	private void saveUsers(String email, String hashedPassword) throws IOException {
		try (FileWriter userAccountsFile = new FileWriter(usersPath, true)) {
			// Appending the new user to the accounts file
			userAccountsFile.append("\n" + String.format("%s, %s", email, hashedPassword));
		}

		// Creating user database
		Path userNewPath = Paths.get(String.format("src/resources/individual-accounts/%s.txt", email));

		// For double checking
		if (!Files.exists(userNewPath)) {
			Files.createFile(userNewPath);
		}
	}

	private String getHashedPassword(String plainPassword) {
		return Base64.getEncoder().encodeToString(plainPassword.getBytes());
	}
}
