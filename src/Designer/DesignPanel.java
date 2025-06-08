package Designer;

import Designer.DesignComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 * The main design canvas where components are dropped and arranged
 * Handles drag-and-drop, selection, moving, resizing, and zooming of components
 * Features Pan Mode and Selection Mode for different interaction types
 */
public class DesignPanel extends JPanel implements DropTargetListener {
    private List<DesignComponent> components;
    private DesignComponent selectedComponent;
    private Point dragOffset;
    private boolean dragging = false;
    private PropertyPanel propertyPanel;
    private ResizeHandle activeResizeHandle;
    private boolean resizing = false;

    // Zoom functionality
    private double zoomFactor = 1.0;
    private static final double MIN_ZOOM = 0.25;
    private static final double MAX_ZOOM = 4.0;
    private static final double ZOOM_STEP = 0.25;
    private Point lastPanPoint;
    private int offsetX = 0;
    private int offsetY = 0;

    // Grid and ruler settings
    private boolean showGrid = true;
    private boolean snapToGrid = true;
    private boolean showRulers = false;
    private int gridSize = 10;
    private Color gridColor = new Color(200, 200, 200);
    private Color rulerColor = new Color(240, 240, 240);
    private Color rulerTextColor = new Color(100, 100, 100);

    // Mode system
    private InteractionMode currentMode = InteractionMode.SELECTION;
    private ModeChangeListener modeChangeListener;

    // Resize handle types
    public enum ResizeHandle {
        NONE, NW, N, NE, E, SE, S, SW, W
    }

    public enum InteractionMode {
        SELECTION("Selection Mode", "Select, move, and resize components", Cursor.getDefaultCursor()),
        PAN("Pan Mode", "Pan around the canvas", Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

        private final String displayName;
        private final String description;
        private final Cursor cursor;

        InteractionMode(String displayName, String description, Cursor cursor) {
            this.displayName = displayName;
            this.description = description;
            this.cursor = cursor;
        }

        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public Cursor getCursor() { return cursor; }
    }

    public interface ModeChangeListener {
        void modeChanged(InteractionMode newMode);
    }

    public DesignPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
        components = new ArrayList<>();
        activeResizeHandle = ResizeHandle.NONE;

        // Enable drop operations
        new DropTarget(this, this);

        // Add mouse listeners for component selection and movement
        addMouseListener(new CanvasMouseListener());
        addMouseMotionListener(new CanvasMouseMotionListener());
        addMouseWheelListener(new CanvasMouseWheelListener());

        // Add keyboard support
        setFocusable(true);
        addKeyListener(new CanvasKeyListener());

        // Set initial cursor
        setCursor(currentMode.getCursor());
    }

    public void setModeChangeListener(ModeChangeListener listener) {
        this.modeChangeListener = listener;
    }

    public InteractionMode getCurrentMode() {
        return currentMode;
    }

    public void setInteractionMode(InteractionMode mode) {
        this.currentMode = mode;

        // Clear selection when switching to pan mode
        if (mode == InteractionMode.PAN) {
            selectedComponent = null;
            if (propertyPanel != null) {
                propertyPanel.updateProperties(null);
            }
        }

        // Update cursor
        setCursor(mode.getCursor());

        // Notify listener
        if (modeChangeListener != null) {
            modeChangeListener.modeChanged(mode);
        }

        repaint();
    }

    public void toggleMode() {
        InteractionMode newMode = (currentMode == InteractionMode.SELECTION) ?
                InteractionMode.PAN : InteractionMode.SELECTION;
        setInteractionMode(newMode);
    }

    public void setPropertyPanel(PropertyPanel propertyPanel) {
        this.propertyPanel = propertyPanel;
    }

    public List<DesignComponent> getDesignComponents() {
        return components;
    }

    // Zoom and pan methods
    public void zoomIn() {
        setZoomFactor(Math.min(zoomFactor + ZOOM_STEP, MAX_ZOOM));
    }

    public void zoomOut() {
        setZoomFactor(Math.max(zoomFactor - ZOOM_STEP, MIN_ZOOM));
    }

    public void resetZoom() {
        setZoomFactor(1.0);
        resetPan();
    }

    public void setZoomFactor(double zoom) {
        zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom));
        this.zoomFactor = zoom;

        // Update preferred size based on zoom
        Dimension baseSize = new Dimension(800, 600);
        setPreferredSize(new Dimension(
                (int) (baseSize.width * zoomFactor),
                (int) (baseSize.height * zoomFactor)
        ));

        revalidate();
        repaint();
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public void resetPan() {
        offsetX = 0;
        offsetY = 0;
        repaint();
    }

    // Convert screen coordinates to canvas coordinates (accounting for zoom/pan)
    private Point screenToCanvas(Point screenPoint) {
        return new Point(
                (int) ((screenPoint.x - offsetX - (showRulers ? 20 : 0)) / zoomFactor),
                (int) ((screenPoint.y - offsetY - (showRulers ? 20 : 0)) / zoomFactor)
        );
    }

    // Convert canvas coordinates to screen coordinates (accounting for zoom/pan)
    private Point canvasToScreen(Point canvasPoint) {
        return new Point(
                (int) (canvasPoint.x * zoomFactor + offsetX + (showRulers ? 20 : 0)),
                (int) (canvasPoint.y * zoomFactor + offsetY + (showRulers ? 20 : 0))
        );
    }

    // Grid and ruler control methods
    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        repaint();
    }

    public boolean isSnapToGrid() {
        return snapToGrid;
    }

    public void setSnapToGrid(boolean snapToGrid) {
        this.snapToGrid = snapToGrid;
    }

    public boolean isShowRulers() {
        return showRulers;
    }

    public void setShowRulers(boolean showRulers) {
        this.showRulers = showRulers;
        repaint();
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = Math.max(1, gridSize);
        repaint();
    }

    public Color getGridColor() {
        return gridColor;
    }

    public void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Apply zoom and pan transformation
        AffineTransform transform = new AffineTransform();
        transform.translate(offsetX, offsetY);
        transform.scale(zoomFactor, zoomFactor);
        g2d.setTransform(transform);

        // Draw grid
        drawGrid(g2d);

        // Draw all components
        for (DesignComponent comp : components) {
            comp.draw(g2d);
        }

        // Draw selection handles for selected component (only in selection mode)
        if (selectedComponent != null && currentMode == InteractionMode.SELECTION) {
            drawSelectionHandles(g2d, selectedComponent);
        }

        // Draw mode info
        g2d.dispose();
        drawModeInfo(g);
        drawZoomInfo(g);
    }

    private void drawModeInfo(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Mode indicator in top-left corner
        String modeText = currentMode.getDisplayName();
        Color modeColor = (currentMode == InteractionMode.PAN) ?
                new Color(255, 140, 0, 200) : new Color(50, 150, 50, 200);

        g2d.setColor(modeColor);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(modeText);

        // Draw background for mode text
        g2d.fillRoundRect(10, 10, textWidth + 20, 25, 8, 8);
        g2d.setColor(Color.WHITE);
        g2d.drawString(modeText, 20, 28);

        // Draw mode description
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        g2d.drawString(currentMode.getDescription(), 20, 50);
    }

    private void drawZoomInfo(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        String zoomText = String.format("Zoom: %.0f%%", zoomFactor * 100);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(zoomText);

        // Draw background for zoom text
        g2d.fillRoundRect(getWidth() - textWidth - 20, 10, textWidth + 15, 20, 5, 5);
        g2d.setColor(Color.WHITE);
        g2d.drawString(zoomText, getWidth() - textWidth - 12, 25);
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(Color.LIGHT_GRAY);
        int gridSize = 10;

        for (int x = 0; x < getWidth(); x += gridSize) {
            g2d.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += gridSize) {
            g2d.drawLine(0, y, getWidth(), y);
        }
    }

    private void drawSelectionHandles(Graphics2D g2d, DesignComponent comp) {
        g2d.setColor(Color.BLUE);
        Rectangle bounds = comp.getBounds();

        // Draw selection rectangle
        g2d.drawRect(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4);

        // Draw resize handles
        int handleSize = 8;
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));

        // Corner handles
        drawResizeHandle(g2d, bounds.x - handleSize/2, bounds.y - handleSize/2, handleSize); // NW
        drawResizeHandle(g2d, bounds.x + bounds.width - handleSize/2, bounds.y - handleSize/2, handleSize); // NE
        drawResizeHandle(g2d, bounds.x - handleSize/2, bounds.y + bounds.height - handleSize/2, handleSize); // SW
        drawResizeHandle(g2d, bounds.x + bounds.width - handleSize/2, bounds.y + bounds.height - handleSize/2, handleSize); // SE

        // Edge handles
        drawResizeHandle(g2d, bounds.x + bounds.width/2 - handleSize/2, bounds.y - handleSize/2, handleSize); // N
        drawResizeHandle(g2d, bounds.x + bounds.width - handleSize/2, bounds.y + bounds.height/2 - handleSize/2, handleSize); // E
        drawResizeHandle(g2d, bounds.x + bounds.width/2 - handleSize/2, bounds.y + bounds.height - handleSize/2, handleSize); // S
        drawResizeHandle(g2d, bounds.x - handleSize/2, bounds.y + bounds.height/2 - handleSize/2, handleSize); // W
    }

    private void drawResizeHandle(Graphics2D g2d, int x, int y, int size) {
        g2d.fillRect(x, y, size, size);
        g2d.setColor(Color.BLUE);
        g2d.drawRect(x, y, size, size);
        g2d.setColor(Color.WHITE);
    }

    private ResizeHandle getResizeHandleAt(Point point, Rectangle bounds) {
        int handleSize = 8;
        int tolerance = handleSize / 2;

        // Check corner handles
        if (isPointInHandle(point, bounds.x, bounds.y, tolerance)) return ResizeHandle.NW;
        if (isPointInHandle(point, bounds.x + bounds.width, bounds.y, tolerance)) return ResizeHandle.NE;
        if (isPointInHandle(point, bounds.x, bounds.y + bounds.height, tolerance)) return ResizeHandle.SW;
        if (isPointInHandle(point, bounds.x + bounds.width, bounds.y + bounds.height, tolerance)) return ResizeHandle.SE;

        // Check edge handles
        if (isPointInHandle(point, bounds.x + bounds.width/2, bounds.y, tolerance)) return ResizeHandle.N;
        if (isPointInHandle(point, bounds.x + bounds.width, bounds.y + bounds.height/2, tolerance)) return ResizeHandle.E;
        if (isPointInHandle(point, bounds.x + bounds.width/2, bounds.y + bounds.height, tolerance)) return ResizeHandle.S;
        if (isPointInHandle(point, bounds.x, bounds.y + bounds.height/2, tolerance)) return ResizeHandle.W;

        return ResizeHandle.NONE;
    }

    private boolean isPointInHandle(Point point, int handleX, int handleY, int tolerance) {
        return Math.abs(point.x - handleX) <= tolerance && Math.abs(point.y - handleY) <= tolerance;
    }

    private Cursor getResizeCursor(ResizeHandle handle) {
        switch (handle) {
            case NW: return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
            case N: return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
            case NE: return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
            case E: return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
            case SE: return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
            case S: return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
            case SW: return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
            case W: return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
            default: return Cursor.getDefaultCursor();
        }
    }

    private void handleResize(Point mousePoint) {
        Rectangle bounds = selectedComponent.getBounds();
        int minSize = 20; // Minimum component size

        int newX = bounds.x;
        int newY = bounds.y;
        int newWidth = bounds.width;
        int newHeight = bounds.height;

        switch (activeResizeHandle) {
            case NW:
                newX = Math.min(mousePoint.x, bounds.x + bounds.width - minSize);
                newY = Math.min(mousePoint.y, bounds.y + bounds.height - minSize);
                newWidth = bounds.x + bounds.width - newX;
                newHeight = bounds.y + bounds.height - newY;
                break;
            case N:
                newY = Math.min(mousePoint.y, bounds.y + bounds.height - minSize);
                newHeight = bounds.y + bounds.height - newY;
                break;
            case NE:
                newY = Math.min(mousePoint.y, bounds.y + bounds.height - minSize);
                newWidth = Math.max(mousePoint.x - bounds.x, minSize);
                newHeight = bounds.y + bounds.height - newY;
                break;
            case E:
                newWidth = Math.max(mousePoint.x - bounds.x, minSize);
                break;
            case SE:
                newWidth = Math.max(mousePoint.x - bounds.x, minSize);
                newHeight = Math.max(mousePoint.y - bounds.y, minSize);
                break;
            case S:
                newHeight = Math.max(mousePoint.y - bounds.y, minSize);
                break;
            case SW:
                newX = Math.min(mousePoint.x, bounds.x + bounds.width - minSize);
                newWidth = bounds.x + bounds.width - newX;
                newHeight = Math.max(mousePoint.y - bounds.y, minSize);
                break;
            case W:
                newX = Math.min(mousePoint.x, bounds.x + bounds.width - minSize);
                newWidth = bounds.x + bounds.width - newX;
                break;
        }

        // Snap to grid if enabled
        if (snapToGrid) {
            newX = (newX / gridSize) * gridSize;
            newY = (newY / gridSize) * gridSize;
            newWidth = ((newWidth + gridSize/2) / gridSize) * gridSize; // Round to nearest grid
            newHeight = ((newHeight + gridSize/2) / gridSize) * gridSize;
        }

        // Ensure minimum size
        newWidth = Math.max(newWidth, minSize);
        newHeight = Math.max(newHeight, minSize);

        selectedComponent.setBounds(newX, newY, newWidth, newHeight);
    }

    // DropTargetListener implementation
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_COPY);
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_COPY);
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {}

    @Override
    public void dragExit(DropTargetEvent dte) {}

    @Override
    public void drop(DropTargetDropEvent dtde) {
        try {
            dtde.acceptDrop(DnDConstants.ACTION_COPY);

            var transferable = dtde.getTransferable();
            Point dropPoint = screenToCanvas(dtde.getLocation()); // Convert to canvas coordinates

            if (transferable.isDataFlavorSupported(ComponentTransferHandler.COMPONENT_FLAVOR)) {
                // Standard component
                Class<?> componentClass = (Class<?>) transferable.getTransferData(ComponentTransferHandler.COMPONENT_FLAVOR);
                DesignComponent newComp = new DesignComponent(componentClass, dropPoint.x, dropPoint.y);
                components.add(newComp);
                selectedComponent = newComp;

            }

            // Update property panel for new component
            if (propertyPanel != null && selectedComponent != null) {
                propertyPanel.updateProperties(selectedComponent);
            }

            repaint();
            dtde.dropComplete(true);

        } catch (Exception e) {
            e.printStackTrace();
            dtde.dropComplete(false);
        }
    }

    // Inner classes for event handling
    private class CanvasMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            // Request focus for keyboard support
            requestFocusInWindow();

            // Check if clicking on resize handle of selected component
            if (selectedComponent != null) {
                activeResizeHandle = getResizeHandleAt(e.getPoint(), selectedComponent.getBounds());
                if (activeResizeHandle != ResizeHandle.NONE) {
                    resizing = true;
                    return;
                }
            }

            selectedComponent = null;
            activeResizeHandle = ResizeHandle.NONE;

            // Find clicked component
            for (int i = components.size() - 1; i >= 0; i--) {
                DesignComponent comp = components.get(i);
                if (comp.getBounds().contains(e.getPoint())) {
                    selectedComponent = comp;
                    dragOffset = new Point(
                            e.getX() - comp.getBounds().x,
                            e.getY() - comp.getBounds().y
                    );
                    break;
                }
            }

            // Update property panel
            if (propertyPanel != null) {
                propertyPanel.updateProperties(selectedComponent);
            }

            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            dragging = false;
            resizing = false;
            activeResizeHandle = ResizeHandle.NONE;
            setCursor(Cursor.getDefaultCursor());

            // Update property panel after resize/move
            if (selectedComponent != null && propertyPanel != null) {
                propertyPanel.refreshProperties();
            }
        }
    }

    private class CanvasMouseMotionListener extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            if (currentMode == InteractionMode.PAN) {
                // Handle panning in pan mode
                if (lastPanPoint != null) {
                    Point currentPoint = e.getPoint();
                    offsetX += currentPoint.x - lastPanPoint.x;
                    offsetY += currentPoint.y - lastPanPoint.y;
                    lastPanPoint = currentPoint;
                    repaint();
                }
                return;
            }

            // Selection mode - handle component manipulation
            if (selectedComponent != null && SwingUtilities.isLeftMouseButton(e)) {
                Point canvasPoint = screenToCanvas(e.getPoint());

                if (resizing && activeResizeHandle != ResizeHandle.NONE) {
                    // Handle resizing
                    handleResize(canvasPoint);
                } else if (dragOffset != null) {
                    // Handle moving
                    dragging = true;
                    int newX = canvasPoint.x - dragOffset.x;
                    int newY = canvasPoint.y - dragOffset.y;

                    // Snap to grid if enabled
                    if (snapToGrid) {
                        int effectiveGridSize = (int) Math.max(1, gridSize / zoomFactor);
                        newX = (newX / effectiveGridSize) * effectiveGridSize;
                        newY = (newY / effectiveGridSize) * effectiveGridSize;
                    }

                    selectedComponent.setLocation(newX, newY);
                }

                repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (currentMode == InteractionMode.PAN) {
                // Keep pan cursor in pan mode
                setCursor(currentMode.getCursor());
                return;
            }

            // Selection mode - update cursor based on mouse position over resize handles
            if (selectedComponent != null) {
                Point canvasPoint = screenToCanvas(e.getPoint());
                ResizeHandle handle = getResizeHandleAt(canvasPoint, selectedComponent.getBounds());
                setCursor(getResizeCursor(handle));
            } else {
                setCursor(currentMode.getCursor());
            }
        }
    }

    // Mouse wheel listener for zooming
    private class CanvasMouseWheelListener implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.isControlDown()) {
                // Zoom with Ctrl+Mouse Wheel
                Point mousePoint = e.getPoint();
                Point canvasPoint = screenToCanvas(mousePoint);

                double oldZoom = zoomFactor;
                if (e.getWheelRotation() < 0) {
                    zoomIn();
                } else {
                    zoomOut();
                }

                // Adjust pan to zoom towards mouse cursor
                if (zoomFactor != oldZoom) {
                    double zoomChange = zoomFactor / oldZoom;
                    offsetX = (int) (mousePoint.x - (canvasPoint.x * zoomFactor));
                    offsetY = (int) (mousePoint.y - (canvasPoint.y * zoomFactor));
                    repaint();
                }
            } else {
                // Scroll normally if not holding Ctrl
                getParent().dispatchEvent(e);
            }
        }
    }

    private class CanvasKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            // Mode switching with Space key
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                toggleMode();
                return;
            }

            // Component manipulation (only in selection mode)
            if (currentMode == InteractionMode.SELECTION && selectedComponent != null) {
                Rectangle bounds = selectedComponent.getBounds();
                boolean moved = false;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DELETE:
                        components.remove(selectedComponent);
                        selectedComponent = null;
                        if (propertyPanel != null) {
                            propertyPanel.clearSelection();
                        }
                        moved = true;
                        break;
                    case KeyEvent.VK_LEFT:
                        selectedComponent.setLocation(bounds.x - 10, bounds.y);
                        moved = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        selectedComponent.setLocation(bounds.x + 10, bounds.y);
                        moved = true;
                        break;
                    case KeyEvent.VK_UP:
                        selectedComponent.setLocation(bounds.x, bounds.y - 10);
                        moved = true;
                        break;
                    case KeyEvent.VK_DOWN:
                        selectedComponent.setLocation(bounds.x, bounds.y + 10);
                        moved = true;
                        break;
                }

                if (moved) {
                    if (propertyPanel != null && selectedComponent != null) {
                        propertyPanel.refreshProperties();
                    }
                    repaint();
                }
            }

            // Pan with arrow keys in pan mode
            if (currentMode == InteractionMode.PAN) {
                boolean panned = false;
                int panStep = 20;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        offsetX += panStep;
                        panned = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        offsetX -= panStep;
                        panned = true;
                        break;
                    case KeyEvent.VK_UP:
                        offsetY += panStep;
                        panned = true;
                        break;
                    case KeyEvent.VK_DOWN:
                        offsetY -= panStep;
                        panned = true;
                        break;
                }

                if (panned) {
                    repaint();
                }
            }
        }
    }
}