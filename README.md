# Next

Next is a task manager that makes managing your daily tasks simple.

By combining the richness of a graphical interface with the speed of traditional commandline input, Next offers maximum efficiency and ease of use.

## Usage

- [Download the latest release](https://github.com/cs2103aug2015-f11-2j/main/releases)
- [**User Guide**](docs/user_guide.md)

## Quick Start

#### Add a task

Tasks can be an event (range of dates) or a deadline (single date). It can also be given a priority level. Next accepts dates in a variety of formats and also supports relational dates, such as "today", "tomorrow", and "monday".

```
# Overview
add <task> [from <date> to <date>] [by <date>] [priority high | medium | low]

# Examples
add finish project documentation by 09/11/2015 8pm priority high
add project presentation from 13/11/15 1:30pm to 2pm
```

#### Edit a task

To edit a task, specify the ID of the task you wish to edit. Each task component (content, date, priority) can be individually updated by only specifying the new value of that component.

```
# Overview 
edit <id> [task] [from <date> to <date>] [by <date>] [priority high | medium | low]

# Examples
edit 1 by 09/11/2015 10pm
edit 2 priority high
```

#### Mark tasks as completed

Multiple IDs can be specified by separating them with commas or spaces. The `all` option can be used to mark all tasks as completed.

```
# Overview 
mark <id | all>

# Examples
mark 1 2 3
mark all
```

#### Search

The search feature supports the ability to search for a combination of individual task components.

```
# Overview 
search [keyword] [between <date> and <date> | after <date> | before <date> | date none] [priority high | medium | low | none] [type comp | pend]

# Examples
search project
search by 09/11/2015 10pm priority high
```

#### More options

For information on all of Next's supported commands, use the `help` command or visit the [**User Guide**](docs/user_guide.md)!