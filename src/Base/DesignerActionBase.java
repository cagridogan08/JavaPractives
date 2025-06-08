package Base;

import Utils.Constants;

import javax.swing.AbstractAction;
import javax.swing.Icon;

public abstract class DesignerActionBase extends AbstractAction {

    public DesignerActionBase(String actionName) {
    }

    public DesignerActionBase(String actionName, Icon icon) {
        super(actionName, icon);
        putValue(Constants.ActionExecuteStatus,Boolean.TRUE);
    }

    protected void setExecuted(boolean executed) {
        putValue(Constants.ActionExecuteStatus,executed);
    }

    public boolean isExecuted() {
        return (Boolean)getValue(Constants.ActionExecuteStatus);
    }
    public abstract void init();

}
