package reminder_app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import project_exceptions.DueDateException;
import project_exceptions.MessageException;
import project_exceptions.PriorityException;

// This class uses csv file to get reminders, modify it, and save it.
public class ReminderManager implements ReminderService {
	// About user
	private String email;
	private String reminderFilePath;
	private boolean isLoggedOut = false;

	// Reminders of the user
	private List<Reminder> reminders = new ArrayList<>();

	public ReminderManager(String email) throws FileNotFoundException, IOException {
		this.email = email;
		reminderFilePath = String.format("src/resources/individual-accounts/%s.txt", email);

		loadReminders();

		// Sort the reminders based on the priority
		// HIGH, MEDIUM, LOW
		reminders.sort(getPriorityComparator());
	}

	@Override
	public Reminder addReminder(Reminder reminder) {
		// The reminder toString() already formatted it to a csv style
		// it is safe to add to the reminders
		reminders.add(reminder);

		return reminder;
	}

	@Override
	public Reminder deleteReminder(int index) {
		return reminders.remove(index);
	}

	@Override
	public Reminder markReminder(int index) {
		Reminder reminderToMark = reminders.get(index);
		reminderToMark.setCompleted(true);
		return reminderToMark;
	}

	public List<Reminder> getReminderByDueDate(LocalDate dueDateInput) {
		// When using add and defined list
//		List<Reminder> definedDueDateReminders = new ArrayList<>();
//		reminders.stream().filter(e -> e.getDate().isBefore(dueDateInput) || e.getDate().isEqual(dueDateInput))
//				.forEach(definedDueDateReminders::add);
//		return definedDueDateReminders;

		// More cleaner
		return reminders.stream().filter(

				// if date is 23 and due date is 28
				// the reminder is 25, then this reminder is true.
				// Because 25 is not before 23 and 25 is not after 28
				reminder -> !reminder.getDate().isBefore(LocalDate.now()) && !reminder.getDate().isAfter(dueDateInput))
				.collect(Collectors.toList());
	}

	public List<Reminder> getReminderByPriority(Priority filteredPriority) {
		return reminders.stream().filter(e -> e.getPriority() == filteredPriority).collect(Collectors.toList());
	}

	public List<Reminder> getReminderByPastDue() {
		// If due date is before today
		// Ex : 16 due is before 18 today = true, therefore this reminder is past due
		return reminders.stream().filter(e -> e.getDate().isBefore(LocalDate.now())).collect(Collectors.toList());
	}

	// Improve this using stream
	public List<Reminder> clearReminders() {
		List<Reminder> clearedReminders = new LinkedList<>();

		for (int i = 0; i < reminders.size(); i++) {
			if (reminders.get(i).isCompleted() == true) {
				// the removed reminder will return the reminder then will be added to the
				// cleared reminders
				clearedReminders.add(reminders.remove(i));
			}
		}

		return clearedReminders;
	}

	// Changing priority
	public Reminder changeReminderPriority(int index, Priority newPriority) throws PriorityException {
		Reminder reminderToChange = reminders.get(index);

		// Reminder priority and new priority should not be equal
		if (reminderToChange.getPriority() != newPriority) {
			reminderToChange.setPriority(newPriority);

			// This is redundant, unless changing a different object
//			// Setting back to the reminders container
//			reminders.set(index, reminderToChange);
			return reminderToChange;
		}

		// Make this an exception if the condition doesn't met
		throw new PriorityException("Priority is already " + reminderToChange.getPriority());
	}

	// Changing due date
	public Reminder changeReminderDueDate(int index, LocalDate newDueDate) throws DueDateException {
		Reminder reminderToChange = reminders.get(index);

		// Reminder due date should not be equal to the existing due date and should be
		// greater than today
		if (!reminderToChange.getDate().isEqual(newDueDate) && reminderToChange.getDate().isAfter(LocalDate.now())) {
			reminderToChange.setDueDate(newDueDate);

			// This is redundant, unless changing a different object
			// Setting back to the reminders container
//			reminders.set(index, reminderToChange);
			return reminderToChange;
		}

		// Make this an exception if the condition doesn't met

		throw new DueDateException("Due date is already " + reminderToChange.getDate());
	}

	public Reminder changeReminderMessage(int index, String message) throws MessageException {
		Reminder reminderToChange = reminders.get(index);

		if (!reminderToChange.getMessage().trim().equals(message)) {
			reminderToChange.setMessage(message);

			return reminderToChange;
		}

		throw new MessageException("Message is already " + reminderToChange.getMessage());
	}

	// Save the reminders to the csv
	public String saveReminders() throws IOException {
		// The reminders will be sorted again if user saved it
		reminders.sort(getPriorityComparator());

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(reminderFilePath))) {
			for (Reminder line : reminders) {
				// Making it string
				bw.write(line.toString());
				bw.newLine();
			}
		}

		return "Saved Successfully!";
	}

	// TODO
	// 4. Display Reminders
	// - Based on Due Date

	// - Based on Priority
	// - Sorted HIGH, MEDIUM, LOW
	// - Can print specific priority

	// 5. Clear Reminders (Completed or all)
	// 6. Change priority and due date (If completed, cannot use this)
	// 7. Save (If user didn't save, the modified reminders will not saved to csv
	// file)

	// This will load the list of reminders then stores it to the lists
	private void loadReminders() throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(reminderFilePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				String parts[] = line.trim().split(", ");

				// Just make sure that the csv is accurate
				LocalDate dueDate = LocalDate.parse(parts[0]);
				Priority priority = Priority.valueOf(parts[1]);
				String message = parts[2];
				boolean isCompleted = Boolean.parseBoolean(parts[3]);

				Reminder reminder = new Reminder(dueDate, priority, message, isCompleted);
				reminders.add(reminder);
			}
		}
	}

	// Comparator logic
	private Comparator<Reminder> getPriorityComparator() {
		return (s1, s2) -> Integer.compare(getTypePriorityNum(s1.getPriority()), getTypePriorityNum(s2.getPriority()));
	}

	private int getTypePriorityNum(Priority priority) {
		switch (priority) {
		case HIGH:
			return 0;
		case MEDIUM:
			return 1;
		case LOW:
			return 2;
		default:
			return 3;
		}
	}

	public static LocalDate nowPlusDays(int plusDays) {
		return LocalDate.now().plusDays(plusDays);
	}

	public int getRemindersCount() {
		return reminders.size();
	}

	// For user's logout
	public void logout() {
		isLoggedOut = true;
	}

	public boolean isLoggedOut() {
		return isLoggedOut;
	}

	// Getters and Setters
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	// This will also sort the list whenever they will display or get.
	// For making sure the list is in ordered whenever they will perform
	// modification
	public List<Reminder> getReminders() {
		reminders.sort(getPriorityComparator());

		return reminders;
	}
}
