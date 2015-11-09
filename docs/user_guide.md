# User Guide

## Getting started

1. Download the latest executable JAR file from the [releases](https://github.com/cs2103aug2015-f11-2j/main/releases) page
2. Click on the JAR file to start using Next. Ensure Java is installed on the desired machine. Click [here](https://www.java.com/en/download/installed.jsp) to check if your machine has the required Java version.

## Adding a task

- Command: `add <task> [from <date> to <date>] [by <date>] [priority high | medium | low]`
- Aliases: `+, a`

While entering command for adding a task, the bottom of the program will be extended to provide visual feedback to the user, showing the user how the program is reading the user’s input.

#### Specifying priority

- Priority keywords: `priority, p, pri`
- Priority levels: `high, medium, low`

Each priority level has a corresponding color for visual feedback. Red represents High, yellow represents medium, and Green represents low. The default priority has no indicator.

#### Specifying dates

- Start-date keywords: `start, from, begin`
- End-date keywords: `by, due, end, to`
- Date/time formats (non-exhaustive): `today, tomorrow, monday-sunday, dd/mm/yy, dd-mm-yy, 3pm, 3:30pm, 1530`

**Examples:** 
```
from tomorrow to sunday
start 1/1/16 end 2/1/16 9pm
by tomorrow 3pm
due 10pm
```

- If no date or time is specified, the task will be added as a todo item.
- If only time is specified, the date will be set as the current day.
- If only date is specified, the default end time is 11:59pm
- When entering a date range without times specified, the default start time is 12am and the default end time is 11:59pm.

## Modifying a task

- Command: `edit <taskID> [<new_task_description>] [from <date> to <date> | by <date> | date none] [priority high | medium | low | none]`
- Aliases: `e, modify, update, change`

The task ID refers to the number displayed beside each task. It is also possible to edit only the task’s description, deadline or priority by not specifying the others.

## Displaying tasks

- Command: `display [uncompleted | completed | all]`
- Aliases: `view, show, v`
- "uncompleted" aliases: `u, uncomp, uncompleted, i, incomp, incomplete, pend, pending`
- "completed aliases": `c, comp, complete, completed`
- "all" aliases: `a, al`

## Marking tasks

- Command: `mark <taskID> [<taskID> <taskID> ...] | all`
- Aliases: `m`

The “all” keyword can be used instead of a task id to mark all currently displayed tasks. Multiple tasks can be marked by separating the task IDs with either comma or a space. 

After an uncompleted task has been marked, its description will have a strikethrough effect, deadline will be greyed out, and background color will differ from unmarked tasks. Subsequently, it will not be displayed on the list of pending tasks. Marking the task again will remove the mentioned visual effects and also allow it to be displayed on the list of pending tasks. The command Mark toggles the completed or uncompleted state of the task.

## Deleting tasks

- Command: `delete <taskID> [<taskID> <taskID> ...]`
- Aliases: `d, del, -, rm, remove`

Multiple tasks can be deleted by separating the task IDs with either comma or a space. Deleted tasks will not be displayed in any list of tasks.

## Searching

- Command: `search <keyword> [between <date> and <date> | after <date> | before <date> | date none] [priority high | medium | low | none] [type comp | pend]`
- Aliases: `s`

Keyword refers to a word in the task description. It is possible to search only by date, priority or type by not specifying the others.

## Undo

- Command: `undo`
- Aliases: `u`

## Change the data storage and log file location

- Command: `save [log] <path>`
- Default log file path: `./logs/next.log`
- Default storage file path: `./next.txt`

## Changing the theme

- Command: `theme [light | dark]`
- Aliases: `t`

## Getting help

- Command: `help [<command>]`
- Aliases: `h, ?`