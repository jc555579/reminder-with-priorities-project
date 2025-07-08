package reminder_app;

public interface ReminderService {
	// Each of these will return the user modified reminder
	// So that it will be easier for prompting
	Reminder addReminder(Reminder reminder);

	Reminder deleteReminder(int index);

	Reminder markReminder(int index);
}
