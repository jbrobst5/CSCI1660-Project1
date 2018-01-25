package com.jack.csci1660.project1;

import java.util.Scanner;

/**
 * Created by brobst.30 on 1/24/18.
 *
 * Most of this is probably overkill, but I wanted to get some practice in while it's still easy.
 */
public class TaskManager {

    /**
     * Nodes for the list (see below)
     **/
    private class Task {
        public String data;
        public Task next;

        public Task(String data) {
            this.data = data;
        }
    }

    /**
     * A list seems more appropriate for this...
     *
     * Very basic 1-direction list
     **/
    private class List {
        private Task first;
        private Task last;
        private int size = 0;

        // Returns the task at [ind] of list
        public Task index(int ind) {
            Task current = this.first;
            for (int i = 0; i < ind; i++) {
                if (current.next == null) {
                    return null;
                }
                current = current.next;
            }
            return current;
        }

        // Adds the new task to the end of the list
        public void append(Task t) {
            this.size++;
            if (this.first == null) {
                this.first = t;
                this.last = t;
                return;
            }
            this.last.next = t;
            this.last = last.next;
        }

        // Deletes the task at [ind] from list
        public Task remove(int ind) {
            if (ind < 0) {return null;}
            if (ind == 0) {
                Task out = this.first;
                this.first = this.first.next;
                this.size--;
                return out;
            }
            Task toRemove = this.index(ind);
            if (toRemove == null) {
                return null;
            }
            Task prev = this.index(ind-1);
            prev.next = toRemove.next;
            if (toRemove.next == null) {
                this.last = prev;
            }
            this.size--;
            return toRemove;
        }

        // Mostly for looping
        public int getSize() {return this.size;}
    }

    /**
     * Should make this more maintainable and chunk out the concerns a little better.
     * It's just for callbacks for later, this would be loads more simple in Python...
     **/
    private interface TaskAction {
        void run();
    }

    //////////////////////////
    // Start Main class code
    //////////////////////////

    // To take in input easier
    private Scanner scanner = new Scanner(System.in);
    // This holds all the tasks the user enters
    private List taskList = new List();

    // Asks the user for information about the new task, and then adds it
    void actionAddTask() {
        System.out.print("Enter a description for the new task (Enter to submit):\n > ");
        String description = scanner.nextLine();
        // Create the new task from the input
        this.taskList.append(new Task(description));
        // Verify
        System.out.printf("New task created. %d tasks total\n", this.taskList.getSize());
    }

    // Asks the user which task to remove and then removes it if possible
    void actionRemoveTask() {
        System.out.print("Enter the index of the task to remove, or c to cancel (Enter to submit):\n > ");
        String in = scanner.nextLine();
        int index;

        // Looking for a c, but don't want to exit here in case the conversion doesn't go well
        try {
            if ((in.charAt(0) == 'c')) {
                return; // Should go back to the menu, but that's up to some other bit of code
            }
        }
        // Don't really care if there's an error here, just move on
        catch (Exception e) {}

        // Looking for a number
        try {
            index = Integer.parseInt(in);
        }
        // If the conversion doesn't go well, just assume the user typed a letter and go back to the menu
        // Probably could handle this a little better in the future
        catch (Exception e) {
            System.out.println("Error parsing input");
            return; // Should go back to the menu, but that's up to some other bit of code
        }

        // Deletes the task and then prints it out for the user's piece of mind
        Task removed = this.taskList.remove(index);
        System.out.printf("Task removed: %s\n", removed.data);
    }

    // Asks the user which task to update and what to update it to, then does so if possible
    void actionUpdateTask() {
        System.out.print("Enter the index of the task to update, or c to cancel (Enter to submit):\n > ");
        String in = scanner.nextLine();
        int index;

        // Handling the cancel option, see above
        try {
            if ((in.charAt(0) == 'c')) {
                return;
            }
        }
        catch (Exception e) {}

        // Handling the number input, see above
        try {
            index = Integer.parseInt(in);
        }
        catch (Exception e) {
            System.out.println("Error parsing input");
            return;
        }

        // Gets the task to update, and makes sure the user typed something in range
        Task toUpdate = this.taskList.index(index);
        if (toUpdate == null) {
            System.out.println("No task at that index");
            return;
        }

        // Gets the new description and sets it, then confirms
        System.out.print("Enter the new description for the task\n > ");
        in = scanner.nextLine();
        toUpdate.data = in;
        System.out.println("Task updated");
    }

    // Prints out all the tasks in order of their index
    void actionListTasks() {
        System.out.println("Here are all your tasks:");
        for (int i = 0; i < this.taskList.getSize(); i++) {
            System.out.printf(" (%d) %s\n", i, this.taskList.index(i).data);
        }
    }

    // Exits cleanly after confirming
    void actionExit() {
        System.out.println("Exiting");
        System.exit(0);
    }

    /*
    Parallel arrays to keep track of the text presented to the user and the corresponding action
    Makes the UI easier to code, is easier to debug, and makes it really easy to make new actions
     */

    // List of text presented to the user
    private String[] actionTexts = {
            "Add a task",
            "Remove a task",
            "Update a task",
            "List all tasks",
            "Exit"
    };
    // List of callbacks that correspond to the texts above (technically they're TaskAction interfaces, but same same)
    private TaskAction[] actions = {
            // Add a task
            new TaskAction() { public void run() { actionAddTask(); } },
            // Remove a task
            new TaskAction() { public void run() { actionRemoveTask(); } },
            // Update a task
            new TaskAction() { public void run() { actionUpdateTask(); } },
            // List all tasks
            new TaskAction() { public void run() { actionListTasks(); } },
            // Exit
            new TaskAction() { public void run() { actionExit(); } }
    };

    // Runs the task manager UI
    public void main() {
        // All those extra classes should make this pretty simple...
        // Infinite loop, user can run the Exit action or initiate a keyboard interrupt
        while (true) {
            // Present all the actions in the actionTexts array
            System.out.println("Please choose an option:");
            for (int i = 0; i < actionTexts.length; i++) {
                System.out.printf("(%d) %s\n", i, actionTexts[i]);
            }
            // Get the input from the user
            String in = scanner.nextLine();
            int index;
            // Similar code to the action functions above
            try {
                index = Integer.parseInt(in);
            }
            catch (Exception e) {
                System.out.println("Error parsing input");
                continue;
            }
            // Spacing for readability
            System.out.print("\n");
            // Run the appropriate action -- it's important that those two arrays match up
            actions[index].run();
            // Spacing for readability
            System.out.print("\n");
        }
    }
}
