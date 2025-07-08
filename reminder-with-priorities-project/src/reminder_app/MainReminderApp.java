package reminder_app;

import java.io.IOException;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import account_manager.AuthenticationSystem;
import project_exceptions.DueDateException;
import project_exceptions.MessageException;
import project_exceptions.PriorityException;

public class MainReminderApp {

	public static void main(String[] args) {
		boolean isExitting = false;

		try (Scanner scanner = new Scanner(System.in)) {
			AuthenticationSystem auth = new AuthenticationSystem();

			while (!isExitting) {

				System.out.println("------------------------------------------------");
				System.out.println("Welcome to Reminder with Priority Application!");
				System.out.println("\t 1. Login");
				System.out.println("\t 2. Sign-up");
				System.out.println("\t 3. Exit");
				System.out.println("------------------------------------------------");
				System.out.print("[1-3] Input : ");
				String accountOption = scanner.nextLine();

				switch (accountOption) {
				case "1":
					System.out.println("Please enter your username and password");
					System.out.print("Email    : ");
					String email = scanner.nextLine();

					System.out.print("Password : ");
					String password = scanner.nextLine();

					if (auth.login(email, password)) {
						ReminderManager reminderManager = new ReminderManager(email);

						System.out.println("Welcome, " + email + "!");
						while (!reminderManager.isLoggedOut()) {
							try {
								System.out.println("------------------------------------------------");
								System.out.println("\t Reminder's App Menu");
								System.out.println("\t 1. Add Reminder");
								System.out.println("\t 2. Delete Reminder ");
								System.out.println("\t 3. Mark Reminder");
								System.out.println("\t 4. Display Reminder/s");
								System.out.println("\t 5. Clear Marked Reminders");
								System.out.println("\t 6. Change Reminder Detail");
								System.out.println("\t 7. Save All Modifications");
								System.out.println("\t 8. Logout");
								System.out.println("------------------------------------------------");
								System.out.print("[1-8] Input : ");
								String menuOption = scanner.nextLine();

								switch (menuOption) {
								case "1": // Add
									// Maximum reminders per user is 15
									// Needs for reminder
									// Due date, priority, message
									if (reminderManager.getReminders().size() <= 15) {
										try {
											// Due date to add
											LocalDate dueDateToAdd = dueDateInput(scanner);

											// Priority input
											Priority priorityToAdd = priorityInput(scanner);

											// User's reminder message
											String messageToAdd = messageInput(scanner);

											// Putting it to a reminder object before passing it to the reminder manager
											Reminder reminderToAdd = reminderManager.addReminder(
													new Reminder(dueDateToAdd, priorityToAdd, messageToAdd));
											if (reminderToAdd != null) {
												System.out.println(
														reminderToAdd + "\nThis reminder is successfully added!");
											} else {
												System.out.println("Unavailable to add! Please check your inputs!");
											}
										} catch (InputMismatchException e) {
											System.out.println("An error occurred: Invalid Input!");
											scanner.nextLine(); // Providing next line for input
										} catch (DueDateException | PriorityException | MessageException e) {
											System.out.println("An error occurred: " + e.getMessage());
										} catch (Exception e) {
											System.out.println("Something went wrong!");
										}
									} else {
										System.out.println("You have reached the limit of reminders!");
										System.out.println(
												"Kindly remove a reminder or clear reminder/s that has been completed!");
									}

									break;
								case "2": // Delete
									List<Reminder> remindersForDelete = reminderManager.getReminders();

									int reminderNumberForDelete = reminderNumberInput(remindersForDelete, scanner);

									// - 1 for index
									Reminder deletedReminder = reminderManager
											.deleteReminder(reminderNumberForDelete - 1);

									if (deletedReminder != null) {
										System.out.println(deletedReminder + "\nThis reminder is deleted successfuly!");
									} else {
										System.out.println("Failed to delete the reminder!");
										System.out.println("Please check your inputs!");
									}

									break;
								case "3": // Mark
									List<Reminder> remindersForMark = reminderManager.getReminders();

									int reminderNumberForMarking = reminderNumberInput(remindersForMark, scanner);
									// - 1 for index
									Reminder markedReminder = reminderManager
											.markReminder(reminderNumberForMarking - 1);

									if (markedReminder != null) {
										System.out.println(markedReminder + "\nThis reminder is marked successfuly!");
									} else {
										System.out.println("Failed to marked as done!");
										System.out.println("Please check your inputs!");
									}

									break;
								case "4": // Display
									// Checking first the reminders if it's empty, before performing display options
									if (!reminderManager.getReminders().isEmpty()) {
										System.out.println("------------------------------------------------");
										System.out.println("\t Kindly pick display options");
										System.out.println("\t 1. Display by chosen due date");
										System.out.println("\t 2. Display by chosen priority");
										System.out.println("\t 3. Display if past due date");
										System.out.println("\t 4. Display All");
										System.out.println("------------------------------------------------");
										System.out.print("[1-4] Input : ");
										String displayOption = scanner.nextLine();

										switch (displayOption) {
										case "1": // Due date
											// Instruction
											LocalDate filteredDueDate = dueDateInput(scanner);

											List<Reminder> filteredDueDateList = reminderManager
													.getReminderByDueDate(filteredDueDate);

											printReminders(filteredDueDateList);

											break;
										case "2": // Priority
											// This can't hold a null, it will have an exception
											Priority priorityToDisplay = priorityInput(scanner);

											List<Reminder> priorityReminderList = reminderManager
													.getReminderByPriority(priorityToDisplay);

											printReminders(priorityReminderList);
											break;
										case "3": // Past due
											printReminders(reminderManager.getReminderByPastDue());
											break;
										case "4": // All
											printReminders(reminderManager.getReminders());
											break;
										default:
											System.out.println("Invalid Input! Try again!");
										}
									} else {
									}
									break;
								case "5": // Clear
									List<Reminder> clearedReminders = reminderManager.clearReminders();

									if (!clearedReminders.isEmpty()) {
										System.out
												.println("This reminder is cleared now, save it to become permanent!");
									}
									System.out.println("Reminders that has been cleared.");
									printReminders(clearedReminders);
									break;
								case "6": // Change
									List<Reminder> remindersForChangeList = reminderManager.getReminders();

									// Getting the reminder to change
									// - 1 for index
									int reminderIndexToChange = reminderNumberInput(remindersForChangeList, scanner)
											- 1;
									// This will make an indepent object, thus this reminder will not affect the new
									// reminder.
									Reminder reminderToChange = remindersForChangeList.get(reminderIndexToChange)
											.copy();

									System.out.println(
											"-----------------------------------------------------------------");
									System.out
											.println("For changing a reminder, it must be different from what it was!");
									System.out.println("\t 1. Change Priority");
									System.out.println("\t 2. Change Due Date");
									System.out.println("\t 3. Change Message");
									System.out.println(
											"-----------------------------------------------------------------");

									// This is to ensure that user will enter 1-3
									Set<String> validOptions = Set.of("1", "2", "3");
									String changeOption;

									while (true) {
										System.out.print("[1-3] Input : ");
										changeOption = scanner.nextLine();

										if (validOptions.contains(changeOption)) {
											break;
										}

										System.out.println("Please input [1-3] only!");
									}

									// Initialized first to avoid unitialized error
									Reminder updatedReminder = null;
									System.out.println("This the Current Reminder Structure\n" + reminderToChange);
									switch (changeOption) {
									case "1":
										Priority priorityToChange = priorityInput(scanner);
										updatedReminder = reminderManager.changeReminderPriority(reminderIndexToChange,
												priorityToChange);
										break;
									case "2":
										LocalDate dueDateToChange = dueDateInput(scanner);
										updatedReminder = reminderManager.changeReminderDueDate(reminderIndexToChange,
												dueDateToChange);
										break;
									case "3":
										String messageToChange = messageInput(scanner);
										updatedReminder = reminderManager.changeReminderMessage(reminderIndexToChange,
												messageToChange);
										break;
									}

									// FIX OLD REMINDER
									System.out.println("The reminder has been updated successfully!");
									System.out.println("Old reminder    : " + reminderToChange);
									System.out.println("Update reminder : " + updatedReminder);

									break;
								case "7": // Save
									// It will return a message if successfull
									System.out.println(reminderManager.saveReminders());
									break;
								case "8": // Logout
									reminderManager.logout();
									System.out.println("Logout Successfull!");
									System.out.println("See you around, " + email + "!");
									break;
								default:
									System.out.println("Invalid Input!");
								}
							} catch (IOException e) {
								System.out.println("An error occurred: " + e.getMessage());
							} catch (InputMismatchException e) {
								System.out.println("An error occurred: Invalid Input!");
								scanner.nextLine(); // Providing next line for input
							} catch (Exception e) {
								System.out.println("An error occurred: " + e.getMessage());
							}

						}
					} else {
						System.out.println("Login failed! Check your email or password!.");
					}
					break;

				case "2":
					System.out.println("For registration, kindly enter your email and password");
					System.out.println("The email should be unique! ");
					System.out.println("Make sure your password is strong!");
					System.out.print("Email      : ");
					String registrationEmail = scanner.nextLine();

					System.out.print("Password : ");
					String registrationPassword = scanner.nextLine();

					System.out.print("Confirm Password : ");
					String confirmPassword = scanner.nextLine();

					if (registrationPassword.equals(confirmPassword)) {
						if (auth.register(registrationEmail, registrationPassword)) {
							System.out.println(
									"Account has been created! Please restart, then you can proceed to log-in!");
						} else {
							System.out.println("Email is already existed! Can't register!");
						}
					} else {
						System.out.println("Password doesn't match! Can't register!");
					}

					break;

				case "3":
					isExitting = true;
					System.out.println("Thank you for using our application! Come again!");
					break;

				default:
					System.out.println("Invalid Input! Must be [1-3] only!");
				}

			}
		} catch (IOException e) {
			System.out.println("Some files didn't behave properly!");
		} catch (Exception e) {
			System.out.println("Something went wrong!");
		}
	}

	// Common methods use for the project
	private static LocalDate dueDateInput(Scanner scanner) throws DueDateException {
		System.out.println("Be careful! If you do invalid input, you will start from the top!");
		System.out.println("--------------------------------------------");
		System.out.println("Date today : " + LocalDate.now());
		System.out.println("Kindly add days to determine the due date");
		System.out.println("2025-05-20 + 7 = 2025-05-27");
		System.out.println("2025-05-27 is the due date");
		System.out.println("Only [1-31] input is allowed!");
		System.out.println("--------------------------------------------");
		System.out.print("Enter days to add : ");
		int daysToAdd = scanner.nextInt();

		if (daysToAdd < 0 || daysToAdd > 31) {
//			scanner.nextLine(); // new line for input
			throw new DueDateException("Only [0-31] input is allowed! For adding days");
		}

		scanner.nextLine(); // New line, because of daysToAdd input

		// Due date to add
		return ReminderManager.nowPlusDays(daysToAdd);
	}

	private static Priority priorityInput(Scanner scanner) throws PriorityException {
		System.out.println("--------------------------------------------");
		System.out.println("\t 1. LOW");
		System.out.println("\t 2. MEDIUM");
		System.out.println("\t 3. HIGH");
		System.out.println("--------------------------------------------");
		System.out.print("Enter Priority : ");
		String priorityOption = scanner.nextLine();

		Priority priority;
		switch (priorityOption) {
		case "1":
			priority = Priority.LOW;
			break;
		case "2":
			priority = Priority.MEDIUM;
			break;
		case "3":
			priority = Priority.HIGH;
			break;
		default:
			throw new PriorityException("Priority option is not found!");
		}

		return priority;
	}

	private static String messageInput(Scanner scanner) throws MessageException {
		System.out.print("Message : ");
		String message = scanner.nextLine();

		// If message is empty
		if (message.isEmpty()) {
			throw new MessageException("Message is Empty!");
		}

		// Sanitized Message
		return message.replaceAll(",", " - ");
	}

	private static void printReminders(List<Reminder> reminders) {
		if (reminders.size() > 0) {
			System.out.println("Some reminders might not be sorted accurately.");
			System.out.println("You need to save it.");
			System.out.println("-----------------------------------------------------------------------------");
			for (int i = 0; i < reminders.size(); i++) {
				System.out.println((i + 1) + ". " + reminders.get(i));
			}
			System.out.println("-----------------------------------------------------------------------------");

		} else {
			System.out.println("No reminders has been found!");
		}
	}

	private static int reminderNumberInput(List<Reminder> reminders, Scanner scanner) throws Exception {
		// Checking first if it has reminder
		int remNum;

		if (!reminders.isEmpty()) {
			System.out.println("Here are the reminders with number.");
			printReminders(reminders);
			System.out.printf("[1-%d] Reminder Number Input : ", reminders.size());
			remNum = scanner.nextInt();

			if (remNum < 0 || remNum > reminders.size()) {
				// New line, because of remNum input
				scanner.nextLine();

				throw new Exception("Reminder number doesn't exist!");
			}
		} else {
			throw new Exception("Reminders is empty!");
		}

		scanner.nextLine(); // New line, because of remNum input

		return remNum;
	}

	public static String sanitizeMessage(String msg) {
		if (msg == null || msg.isEmpty()) {
			return "No message has been found!";
		}

		return msg.replaceAll(", ", " - ");
	}
}
