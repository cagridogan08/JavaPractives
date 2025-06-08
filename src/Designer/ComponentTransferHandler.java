package Designer;

import javax.swing.*;
import java.awt.datatransfer.*;

/**
 * Transfer handler for drag and drop operations
 * Handles transferring component types from palette to canvas
 */
public class ComponentTransferHandler extends TransferHandler {
    public static final DataFlavor COMPONENT_FLAVOR = new DataFlavor(Class.class, "ComponentClass");

    private Class<?> componentClass;

    public ComponentTransferHandler(Class<?> componentClass) {
        this.componentClass = componentClass;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return new ComponentTransferable(componentClass);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY;
    }

    /**
     * Transferable implementation for component classes
     */
    private static class ComponentTransferable implements Transferable {
        private Class<?> componentClass;

        public ComponentTransferable(Class<?> componentClass) {
            this.componentClass = componentClass;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{COMPONENT_FLAVOR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return COMPONENT_FLAVOR.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (isDataFlavorSupported(flavor)) {
                return componentClass;
            }
            throw new UnsupportedFlavorException(flavor);
        }
    }
}