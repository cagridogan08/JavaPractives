package Actions;

import Base.DesignerActionBase;
import Designer.DesignPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class CopyAction extends DesignerActionBase {
    private DesignPanel designPanel;

    public CopyAction(DesignPanel designPanel) {
        super("Copy",null);
        this.designPanel = designPanel;
        this.setEnabled(false);
        putValue(SHORT_DESCRIPTION, "Copy");
        putValue(MNEMONIC_KEY, KeyEvent.VK_C);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
    }

    @Override
    public void init() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
