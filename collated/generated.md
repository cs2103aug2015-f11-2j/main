# generated
###### bin\app\view\fxml\InfoView.fxml
``` fxml
<?import javafx.geometry.*?>
<?import java.lang.*?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.layout.AnchorPane?>

<VBox fx:id="infoViewLayout" maxWidth="1.7976931348623157E308"
	minHeight="-Infinity" minWidth="-Infinity" styleClass="infoView"
	xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1"
	fx:controller="app.view.InfoViewManager">
</VBox>
```
###### bin\app\view\fxml\InputView.fxml
``` fxml
<?import javafx.scene.layout.*?>
<?import java.lang.*?>
<?import javafx.scene.control.*?>
<?import javafx.scene.text.*?>
<?import javafx.scene.layout.AnchorPane?>
<?import javafx.scene.control.TextField?>

<AnchorPane fx:id="inputViewLayout" maxHeight="1.7976931348623157E308"
	maxWidth="1.7976931348623157E308" AnchorPane.bottomAnchor="0.0"
	AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0"
	xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1"
	fx:controller="app.view.InputViewManager">
	<children>
		<TextField fx:id="commandInput" maxHeight="1.7976931348623157E308"
			maxWidth="1.7976931348623157E308" onAction="#onKeypressEnter"
			prefHeight="35.0" prefWidth="600.0" promptText="Command" styleClass="commandInput"
			AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="0.0"
			AnchorPane.rightAnchor="0.0">
			<font>
				<Font size="16.0" />
			</font>
		</TextField>
	</children>
</AnchorPane>
```
###### bin\app\view\fxml\RootView.fxml
``` fxml
<?import java.net.*?>
<?import javafx.geometry.*?>
<?import javafx.scene.text.*?>
<?import javafx.scene.control.*?>
<?import java.lang.*?>
<?import javafx.scene.layout.*?>

<BorderPane maxHeight="1.7976931348623157E308" maxWidth="1.7976931348623157E308"
	prefHeight="400.0" prefWidth="600.0" xmlns="http://javafx.com/javafx/8"
	xmlns:fx="http://javafx.com/fxml/1" fx:controller="app.view.ViewManager">
	<top>
		<Label fx:id="header" maxWidth="1.7976931348623157E308"
			minHeight="0.0" prefHeight="0.0" styleClass="header"
			BorderPane.alignment="CENTER">
			<padding>
				<Insets bottom="3.0" left="10.0" right="10.0" top="3.0" />
			</padding>
		</Label>
	</top>
	<bottom>
		<VBox prefHeight="42.0" prefWidth="600.0" BorderPane.alignment="CENTER">
			<children>
				<Label fx:id="statusBar" maxWidth="1.7976931348623157E308"
					styleClass="statusBar">
					<padding>
						<Insets bottom="3.0" left="10.0" top="3.0" />
					</padding>
				</Label>
			</children>
		</VBox>
	</bottom>
	<stylesheets>
		<URL value="@../css/base.css" />
		<URL value="@../css/theme_light.css" />
	</stylesheets>
</BorderPane>
```
###### bin\app\view\fxml\TaskListItemView.fxml
``` fxml
<?import javafx.scene.image.*?>
<?import javafx.scene.shape.*?>
<?import javafx.geometry.*?>
<?import javafx.scene.text.*?>
<?import javafx.scene.control.*?>
<?import java.lang.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.layout.StackPane?>

<AnchorPane fx:id="taskListItemViewLayout" maxWidth="1.7976931348623157E308"
	prefWidth="400.0" styleClass="taskItem" xmlns="http://javafx.com/javafx/8"
	xmlns:fx="http://javafx.com/fxml/1" fx:controller="app.view.TaskListItemViewManager">
	<children>
		<HBox alignment="CENTER_RIGHT" centerShape="false" fillHeight="false"
			maxWidth="1.7976931348623157E308" prefHeight="45.0"
			AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="0.0"
			AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="0.0">
			<children>
				<Label fx:id="taskId" alignment="CENTER" maxWidth="60.0"
					minWidth="50.0" prefWidth="50.0" text="ID" HBox.hgrow="ALWAYS">
					<font>
						<Font name="Segoe UI" size="13.0" />
					</font>
					<padding>
						<Insets left="10.0" right="10.0" />
					</padding>
				</Label>
				<HBox styleClass="taskItemLines">
					<children>
						<Line endY="45.0" styleClass="taskItemLine1" />
						<Line endY="45.0" styleClass="taskItemLine2">
							<HBox.margin>
								<Insets left="1.0" />
							</HBox.margin>
						</Line>
						<ImageView fx:id="priorityImage" fitHeight="20.0"
							fitWidth="20.0" pickOnBounds="true" preserveRatio="true"
							styleClass="priorityImage" visible="false">
							<image>
								<Image url="@../images/priority_high_icon.png" />
							</image>
							<HBox.margin>
								<Insets left="-12.0" top="12.0" />
							</HBox.margin>
						</ImageView>
					</children>
				</HBox>
				<Label fx:id="taskName" maxWidth="1.7976931348623157E308"
					prefWidth="300.0" styleClass="taskName" text="Task Name" wrapText="true"
					HBox.hgrow="ALWAYS">
					<font>
						<Font name="Segoe UI Semibold" size="13.0" />
					</font>
					<tooltip>
						<Tooltip fx:id="taskNameTooltip" maxWidth="500.0"
							wrapText="true" />
					</tooltip>
					<padding>
						<Insets left="10.0" right="10.0" />
					</padding>
				</Label>
				<VBox fx:id="taskItemDateVbox" alignment="CENTER_RIGHT"
					nodeOrientation="LEFT_TO_RIGHT" spacing="2.0" styleClass="taskItemDateVbox">
					<HBox.margin>
						<Insets right="10.0" />
					</HBox.margin>
				</VBox>
			</children>
		</HBox>
	</children>
	<opaqueInsets>
		<Insets />
	</opaqueInsets>
</AnchorPane>
```
###### bin\app\view\fxml\TaskListView.fxml
``` fxml
<?import javafx.geometry.*?>
<?import javafx.scene.text.*?>
<?import javafx.scene.control.*?>
<?import java.lang.*?>
<?import javafx.scene.control.ListView?>
<?import javafx.scene.layout.*?>

<VBox maxHeight="1.7976931348623157E308" maxWidth="1.7976931348623157E308"
	xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1"
	fx:controller="app.view.TaskListViewManager">
	<children>
		<ListView fx:id="taskListViewLayout" focusTraversable="false"
			maxHeight="1.7976931348623157E308" maxWidth="1.7976931348623157E308"
			prefHeight="242.0" prefWidth="386.0" AnchorPane.bottomAnchor="38.0"
			AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0"
			AnchorPane.topAnchor="0.0" VBox.vgrow="ALWAYS">
			<placeholder>
				<GridPane maxHeight="1.7976931348623157E308" maxWidth="1.7976931348623157E308"
					prefHeight="400.0" prefWidth="600.0" xmlns="http://javafx.com/javafx/8"
					xmlns:fx="http://javafx.com/fxml/1">
					<columnConstraints>
						<ColumnConstraints hgrow="ALWAYS"
							maxWidth="1.7976931348623157E308" minWidth="10.0" percentWidth="20.0"
							prefWidth="166.0" />
						<ColumnConstraints hgrow="ALWAYS"
							maxWidth="1.7976931348623157E308" minWidth="10.0" prefWidth="287.0" />
						<ColumnConstraints hgrow="ALWAYS"
							maxWidth="1.7976931348623157E308" minWidth="10.0" percentWidth="20.0"
							prefWidth="151.0" />
					</columnConstraints>
					<rowConstraints>
						<RowConstraints maxHeight="129.0" minHeight="10.0"
							percentHeight="25.0" prefHeight="83.0" vgrow="SOMETIMES" />
						<RowConstraints maxHeight="282.0" minHeight="10.0"
							prefHeight="205.0" vgrow="SOMETIMES" />
						<RowConstraints maxHeight="93.0" minHeight="10.0"
							percentHeight="25.0" prefHeight="93.0" vgrow="SOMETIMES" />
					</rowConstraints>
					<children>
						<AnchorPane prefHeight="200.0" prefWidth="200.0"
							GridPane.columnIndex="1" GridPane.rowIndex="1">
							<children>
								<Label alignment="CENTER" layoutX="34.0" layoutY="6.0"
									prefHeight="40.0" prefWidth="213.0" styleClass="listViewTitle"
									text="Next" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0"
									AnchorPane.topAnchor="0.0">
									<font>
										<Font name="Segoe UI Semibold" size="28.0" />
									</font>
								</Label>
								<Label alignment="CENTER" layoutX="165.0" layoutY="40.0"
									prefHeight="20.0" styleClass="listViewNoTasks" text="You don't have any tasks yet!"
									AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0"
									AnchorPane.topAnchor="40.0">
									<font>
										<Font name="Segoe UI Semilight" size="14.0" />
									</font>
								</Label>
								<Label alignment="CENTER" layoutX="96.0" layoutY="59.0"
									styleClass="listViewInfo1" text="Begin by adding a task..."
									AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0"
									AnchorPane.topAnchor="70.0">
									<font>
										<Font name="Segoe UI Light" size="17.0" />
									</font>
								</Label>
								<Label alignment="CENTER" layoutY="86.0" styleClass="ListViewAdd"
									text="add &lt;task&gt;" textFill="#008a12"
									AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0"
									AnchorPane.topAnchor="92.0">
									<font>
										<Font name="Segoe UI Semibold" size="18.0" />
									</font>
								</Label>
								<Label alignment="CENTER" layoutX="54.0" layoutY="136.0"
									styleClass="listViewInfo2" text="or type 'help' for more options"
									AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0"
									AnchorPane.topAnchor="136.0">
									<font>
										<Font name="Segoe UI Light" size="17.0" />
									</font>
								</Label>
								<Label alignment="CENTER" layoutY="162.0" styleClass="listViewHelp"
									text="help" textFill="#008a12" AnchorPane.leftAnchor="0.0"
									AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="158.0">
									<font>
										<Font name="Segoe UI Semibold" size="18.0" />
									</font>
								</Label>
							</children>
						</AnchorPane>
					</children>
				</GridPane>
			</placeholder>
		</ListView>
	</children>
</VBox>
```
###### bin\app\view\fxml\TextView.fxml
``` fxml
<?import javafx.scene.text.*?>
<?import javafx.scene.control.*?>
<?import java.lang.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.layout.AnchorPane?>

<AnchorPane xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1" fx:controller="app.view.TextViewManager">
	<children>
		<TextArea fx:id="textArea" editable="false" prefHeight="200.0" prefWidth="200.0" styleClass="textViewTextArea" AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="0.0">
			<font>
				<Font size="14.0" />
			</font>
		</TextArea>
	</children>
</AnchorPane>
```
###### src\app\model\Action.java
``` java
public class Action {
	private ActionType actionType;
	private Object actionObject;
	
	public Action(ActionType actionType, Object actionObject) {
		this.actionType = actionType;
		this.actionObject = actionObject;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	public Object getActionObject() {
		return actionObject;
	}

	public void setActionObject(Object actionObject) {
		this.actionObject = actionObject;
	}

}
```
###### src\app\model\ParserToken.java
``` java
public class ParserToken {
	private int start = -1;
	private int end = -1;

	public boolean isEmpty() {
		return (start == -1 || end == -1);
	}

	public void clear() {
		start = end = -1;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
}
```
###### src\app\model\Task.java
``` java
	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
	
	public ArrayList<RemovableField> getRemoveField() {
		return removeField;
	}

```
###### src\app\model\TaskCell.java
``` java
	public TaskCell(LocalDate labelDate, String style) {
		this.labelDate = labelDate;
		this.style = style;
	}

	public TaskCell(Task task, int index, String style) {
		this.task = task;
		this.index = index;
		this.style = style;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public LocalDate getLabelDate() {
		return labelDate;
	}

	public void setLabelDate(LocalDate labelDate) {
		this.labelDate = labelDate;
	}

	public String getStyle() {
		if (style == null) {
			return "";
		}
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

```
###### src\app\model\ViewState.java
``` java
	public String getStatusMessage() {
		return statusMessage;
	}

	public StatusType getStatusType() {
		return statusType;
	}

	public ViewType getActiveView() {
		return activeView;
	}

	public void setActiveView(ViewType activeView) {
		this.activeView = activeView;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public TaskList getTaskList() {
		return taskList;
	}

	public void setTaskList(TaskList taskList) {
		// Make sure we don't reference an object that exists somewhere else and
		// may be modified
		this.taskList = new TaskList(taskList);
	}

	public String getTextArea() {
		return textArea;
	}

	public void setTextArea(String textArea) {
		this.textArea = textArea;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public void addAction(Action action) {
		actions.add(action);
	}
}
```
###### src\app\view\fxml\InfoView.fxml
``` fxml
<?import javafx.geometry.*?>
<?import java.lang.*?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.layout.AnchorPane?>

<VBox fx:id="infoViewLayout" maxWidth="1.7976931348623157E308"
	minHeight="-Infinity" minWidth="-Infinity" styleClass="infoView"
	xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1"
	fx:controller="app.view.InfoViewManager">
</VBox>
```
###### src\app\view\fxml\InputView.fxml
``` fxml
<?import javafx.scene.layout.*?>
<?import java.lang.*?>
<?import javafx.scene.control.*?>
<?import javafx.scene.text.*?>
<?import javafx.scene.layout.AnchorPane?>
<?import javafx.scene.control.TextField?>

<AnchorPane fx:id="inputViewLayout" maxHeight="1.7976931348623157E308"
	maxWidth="1.7976931348623157E308" AnchorPane.bottomAnchor="0.0"
	AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0"
	xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1"
	fx:controller="app.view.InputViewManager">
	<children>
		<TextField fx:id="commandInput" maxHeight="1.7976931348623157E308"
			maxWidth="1.7976931348623157E308" onAction="#onKeypressEnter"
			prefHeight="35.0" prefWidth="600.0" promptText="Command" styleClass="commandInput"
			AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="0.0"
			AnchorPane.rightAnchor="0.0">
			<font>
				<Font size="16.0" />
			</font>
		</TextField>
	</children>
</AnchorPane>
```
###### src\app\view\fxml\RootView.fxml
``` fxml
<?import java.net.*?>
<?import javafx.geometry.*?>
<?import javafx.scene.text.*?>
<?import javafx.scene.control.*?>
<?import java.lang.*?>
<?import javafx.scene.layout.*?>

<BorderPane maxHeight="1.7976931348623157E308" maxWidth="1.7976931348623157E308"
	prefHeight="400.0" prefWidth="600.0" xmlns="http://javafx.com/javafx/8"
	xmlns:fx="http://javafx.com/fxml/1" fx:controller="app.view.ViewManager">
	<top>
		<Label fx:id="header" maxWidth="1.7976931348623157E308"
			minHeight="0.0" prefHeight="0.0" styleClass="header"
			BorderPane.alignment="CENTER">
			<padding>
				<Insets bottom="3.0" left="10.0" right="10.0" top="3.0" />
			</padding>
		</Label>
	</top>
	<bottom>
		<VBox prefHeight="42.0" prefWidth="600.0" BorderPane.alignment="CENTER">
			<children>
				<Label fx:id="statusBar" maxWidth="1.7976931348623157E308"
					styleClass="statusBar">
					<padding>
						<Insets bottom="3.0" left="10.0" top="3.0" />
					</padding>
				</Label>
			</children>
		</VBox>
	</bottom>
	<stylesheets>
		<URL value="@../css/base.css" />
		<URL value="@../css/theme_light.css" />
	</stylesheets>
</BorderPane>
```
###### src\app\view\fxml\TaskListItemView.fxml
``` fxml
<?import javafx.scene.image.*?>
<?import javafx.scene.shape.*?>
<?import javafx.geometry.*?>
<?import javafx.scene.text.*?>
<?import javafx.scene.control.*?>
<?import java.lang.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.layout.StackPane?>

<AnchorPane fx:id="taskListItemViewLayout" maxWidth="1.7976931348623157E308"
	prefWidth="400.0" styleClass="taskItem" xmlns="http://javafx.com/javafx/8"
	xmlns:fx="http://javafx.com/fxml/1" fx:controller="app.view.TaskListItemViewManager">
	<children>
		<HBox alignment="CENTER_RIGHT" centerShape="false" fillHeight="false"
			maxWidth="1.7976931348623157E308" prefHeight="45.0"
			AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="0.0"
			AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="0.0">
			<children>
				<Label fx:id="taskId" alignment="CENTER" maxWidth="60.0"
					minWidth="50.0" prefWidth="50.0" text="ID" HBox.hgrow="ALWAYS">
					<font>
						<Font name="Segoe UI" size="13.0" />
					</font>
					<padding>
						<Insets left="10.0" right="10.0" />
					</padding>
				</Label>
				<HBox styleClass="taskItemLines">
					<children>
						<Line endY="45.0" styleClass="taskItemLine1" />
						<Line endY="45.0" styleClass="taskItemLine2">
							<HBox.margin>
								<Insets left="1.0" />
							</HBox.margin>
						</Line>
						<ImageView fx:id="priorityImage" fitHeight="20.0"
							fitWidth="20.0" pickOnBounds="true" preserveRatio="true"
							styleClass="priorityImage" visible="false">
							<image>
								<Image url="@../images/priority_high_icon.png" />
							</image>
							<HBox.margin>
								<Insets left="-12.0" top="12.0" />
							</HBox.margin>
						</ImageView>
					</children>
				</HBox>
				<Label fx:id="taskName" maxWidth="1.7976931348623157E308"
					prefWidth="300.0" styleClass="taskName" text="Task Name" wrapText="true"
					HBox.hgrow="ALWAYS">
					<font>
						<Font name="Segoe UI Semibold" size="13.0" />
					</font>
					<tooltip>
						<Tooltip fx:id="taskNameTooltip" maxWidth="500.0"
							wrapText="true" />
					</tooltip>
					<padding>
						<Insets left="10.0" right="10.0" />
					</padding>
				</Label>
				<VBox fx:id="taskItemDateVbox" alignment="CENTER_RIGHT"
					nodeOrientation="LEFT_TO_RIGHT" spacing="2.0" styleClass="taskItemDateVbox">
					<HBox.margin>
						<Insets right="10.0" />
					</HBox.margin>
				</VBox>
			</children>
		</HBox>
	</children>
	<opaqueInsets>
		<Insets />
	</opaqueInsets>
</AnchorPane>
```
###### src\app\view\fxml\TaskListView.fxml
``` fxml
<?import javafx.geometry.*?>
<?import javafx.scene.text.*?>
<?import javafx.scene.control.*?>
<?import java.lang.*?>
<?import javafx.scene.control.ListView?>
<?import javafx.scene.layout.*?>

<VBox maxHeight="1.7976931348623157E308" maxWidth="1.7976931348623157E308"
	xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1"
	fx:controller="app.view.TaskListViewManager">
	<children>
		<ListView fx:id="taskListViewLayout" focusTraversable="false"
			maxHeight="1.7976931348623157E308" maxWidth="1.7976931348623157E308"
			prefHeight="242.0" prefWidth="386.0" AnchorPane.bottomAnchor="38.0"
			AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0"
			AnchorPane.topAnchor="0.0" VBox.vgrow="ALWAYS">
			<placeholder>
				<GridPane maxHeight="1.7976931348623157E308" maxWidth="1.7976931348623157E308"
					prefHeight="400.0" prefWidth="600.0" xmlns="http://javafx.com/javafx/8"
					xmlns:fx="http://javafx.com/fxml/1">
					<columnConstraints>
						<ColumnConstraints hgrow="ALWAYS"
							maxWidth="1.7976931348623157E308" minWidth="10.0" percentWidth="20.0"
							prefWidth="166.0" />
						<ColumnConstraints hgrow="ALWAYS"
							maxWidth="1.7976931348623157E308" minWidth="10.0" prefWidth="287.0" />
						<ColumnConstraints hgrow="ALWAYS"
							maxWidth="1.7976931348623157E308" minWidth="10.0" percentWidth="20.0"
							prefWidth="151.0" />
					</columnConstraints>
					<rowConstraints>
						<RowConstraints maxHeight="129.0" minHeight="10.0"
							percentHeight="25.0" prefHeight="83.0" vgrow="SOMETIMES" />
						<RowConstraints maxHeight="282.0" minHeight="10.0"
							prefHeight="205.0" vgrow="SOMETIMES" />
						<RowConstraints maxHeight="93.0" minHeight="10.0"
							percentHeight="25.0" prefHeight="93.0" vgrow="SOMETIMES" />
					</rowConstraints>
					<children>
						<AnchorPane prefHeight="200.0" prefWidth="200.0"
							GridPane.columnIndex="1" GridPane.rowIndex="1">
							<children>
								<Label alignment="CENTER" layoutX="34.0" layoutY="6.0"
									prefHeight="40.0" prefWidth="213.0" styleClass="listViewTitle"
									text="Next" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0"
									AnchorPane.topAnchor="0.0">
									<font>
										<Font name="Segoe UI Semibold" size="28.0" />
									</font>
								</Label>
								<Label alignment="CENTER" layoutX="165.0" layoutY="40.0"
									prefHeight="20.0" styleClass="listViewNoTasks" text="You don't have any tasks yet!"
									AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0"
									AnchorPane.topAnchor="40.0">
									<font>
										<Font name="Segoe UI Semilight" size="14.0" />
									</font>
								</Label>
								<Label alignment="CENTER" layoutX="96.0" layoutY="59.0"
									styleClass="listViewInfo1" text="Begin by adding a task..."
									AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0"
									AnchorPane.topAnchor="70.0">
									<font>
										<Font name="Segoe UI Light" size="17.0" />
									</font>
								</Label>
								<Label alignment="CENTER" layoutY="86.0" styleClass="ListViewAdd"
									text="add &lt;task&gt;" textFill="#008a12"
									AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0"
									AnchorPane.topAnchor="92.0">
									<font>
										<Font name="Segoe UI Semibold" size="18.0" />
									</font>
								</Label>
								<Label alignment="CENTER" layoutX="54.0" layoutY="136.0"
									styleClass="listViewInfo2" text="or type 'help' for more options"
									AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0"
									AnchorPane.topAnchor="136.0">
									<font>
										<Font name="Segoe UI Light" size="17.0" />
									</font>
								</Label>
								<Label alignment="CENTER" layoutY="162.0" styleClass="listViewHelp"
									text="help" textFill="#008a12" AnchorPane.leftAnchor="0.0"
									AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="158.0">
									<font>
										<Font name="Segoe UI Semibold" size="18.0" />
									</font>
								</Label>
							</children>
						</AnchorPane>
					</children>
				</GridPane>
			</placeholder>
		</ListView>
	</children>
</VBox>
```
###### src\app\view\fxml\TextView.fxml
``` fxml
<?import javafx.scene.text.*?>
<?import javafx.scene.control.*?>
<?import java.lang.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.layout.AnchorPane?>

<AnchorPane xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1" fx:controller="app.view.TextViewManager">
	<children>
		<TextArea fx:id="textArea" editable="false" prefHeight="200.0" prefWidth="200.0" styleClass="textViewTextArea" AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="0.0">
			<font>
				<Font size="14.0" />
			</font>
		</TextArea>
	</children>
</AnchorPane>
```
