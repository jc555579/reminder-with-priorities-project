package reminder_app;

import java.time.LocalDate;

public class Reminder {
	private LocalDate dueDate;
	private Priority priority;
	private String message;
	private boolean isCompleted;

	public Reminder(LocalDate dueDate, Priority priority, String message) {
		this.dueDate = dueDate;
		this.priority = priority;
		this.message = message;
		this.isCompleted = false; // default after creating the reminder
	}

	// If the user wants to load the reminder from their csv
	public Reminder(LocalDate dueDate, Priority priority, String message, boolean isCompleted) {
		this.dueDate = dueDate;
		this.priority = priority;
		this.message = message;
		this.isCompleted = isCompleted;
	}

	// Getters and Setters
	public LocalDate getDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate date) {
		this.dueDate = date;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	// Having an independent copy
	public Reminder copy() {
		return new Reminder(this.dueDate, this.priority, this.message, this.isCompleted);
	}

	// Generally use for csv line
	@Override
	public String toString() {
		return String.format("%s, %s, %s, MARKED = %b", dueDate, priority, message, isCompleted);
	}
}
