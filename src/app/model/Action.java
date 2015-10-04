package app.model;

import app.constants.ViewConstants.ActionType;

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
