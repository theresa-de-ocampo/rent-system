
package rentsystem;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Theresa De Ocampo
 */
public class Style {
    public static final int POINT_SIZE = 28;
    public static final Color AQUA = new Color(169, 221, 217);
    public static final Color BLUEGREEN = new Color(33, 182, 168);
    public static final Color BARLEY = new Color(255, 244, 203);
    public static final Color SMOKE = new Color(238, 238, 238);
    public static final Font HEADER_FONT = new Font("Glacial Indifference", Font.PLAIN, 35);
    public static final Font SUBHEADER_FONT = new Font("Glacial Indifference", Font.PLAIN, 25);
    public static final Font BODY_FONT = new Font("Glacial Indifference", Font.PLAIN, 20);
    public static final Font FIELD_FONT = new Font("Times New Roman", Font.PLAIN, 20);
    public static final Font MONOSPACED_FONT = new Font("Consolas", Font.PLAIN, 14);
    public static final Border FIELD_MARGIN = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    public static final Border THIN_LINE = BorderFactory.createLineBorder(Color.BLACK);
    public static final Border BLUEGREEN_LINE = new MatteBorder(4, 4, 4, 4, BLUEGREEN);
    public static final Border THICK_LINE = BorderFactory.createStrokeBorder(new BasicStroke(5.0f));
    public static final LineBorder ROUND_LINE = new LineBorder(Color.BLACK, 10, true);
    public static final Dimension BUTTON_SIZE = new Dimension(130, 40);
    
    private static class RoundBorderButton implements Border {
        private final int radius;
        RoundBorderButton(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }
    
    private static class PolishRoundBoderButton extends BasicButtonUI {
        @Override
        public void update(Graphics g, JComponent c) {
            /* If platform is not Windows, this block excluding, the last
             *     line, i.e., paint(g, c) should be under a conditional 
             *     statement which is: if (c.isOpaque())
             * The button's opaque attribute should then be set to true
             *     which is handled in the initButtonAttributes method.
             */
            Color fillColor = c.getBackground();

            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();

            if (model.isPressed()) {
                fillColor = fillColor.darker();
            } else if (model.isRollover()) {
                fillColor = fillColor.brighter();
            }

            g.setColor(fillColor);
            g.fillRoundRect(0, 0, c.getWidth(),c.getHeight(), 20, 20);
            paint(g, c);
        }
    }
    
    // Overrides JTextField
    private static class RoundTextField extends JTextField {
        private Shape shape;
        
        public RoundTextField(int size) {
            super(size);
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
             g.setColor(getBackground());
             g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
             super.paintComponent(g);
        }
        
        @Override
        protected void paintBorder(Graphics g) {
             g.setColor(getForeground());
             g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
        }
        
        @Override
        public boolean contains(int x, int y) {
             if (shape == null || !shape.getBounds().equals(getBounds())) {
                 shape = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 15, 15);
             }
             return shape.contains(x, y);
        }
    }
    
    // Overrides JPasswordField
    private static class RoundPasswordField extends JPasswordField {
        private Shape shape;
        
        public RoundPasswordField(int size) {
            super(size);
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
             g.setColor(getBackground());
             g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
             super.paintComponent(g);
        }
        
        @Override
        protected void paintBorder(Graphics g) {
             g.setColor(getForeground());
             g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
        }
        
        @Override
        public boolean contains(int x, int y) {
             if (shape == null || !shape.getBounds().equals(getBounds())) {
                 shape = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 15, 15);
             }
             return shape.contains(x, y);
        }
    }
    
    private static class HeaderMouseListener extends MouseAdapter {
        Font original;

        @Override
        public void mouseEntered(MouseEvent e) {
            Component button = e.getComponent();
            Font font = button.getFont();
            Map attributes = font.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            button.setFont(font.deriveFont(attributes));
        }

        @Override
        public void mouseExited(MouseEvent e){
            Component button = e.getComponent();
            Font font = button.getFont();
            Map attributes = font.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, null);
            button.setFont(font.deriveFont(attributes));
        }
    }
    
    public static JButton createButton(String label){
        JButton b = new JButton(label);
        b.setFont(Style.BODY_FONT);
        b.setBackground(Style.BLUEGREEN);
        b.setPreferredSize(Style.BUTTON_SIZE);
        b.setBorder(new RoundBorderButton(20));
        b.setUI(new PolishRoundBoderButton());
        b.setOpaque(false);
        return b;
    }
    
    public static void centerFrame(JFrame frame){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
        frame.setResizable(false);
    }
    
    public static TitledBorder createSeparator(String title, Font f){
        MatteBorder mb = new MatteBorder(1, 0, 0, 0, Color.BLACK);
        TitledBorder tb = new TitledBorder(mb, title, TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
        tb.setTitleFont(f);
        return tb;
    }
    
    public static JLabel createLabel(String label, int leftMargin){
        JLabel l = new JLabel(label);
        l.setBorder(new EmptyBorder(0, leftMargin, 0, 10));
        l.setFont(Style.BODY_FONT);
        return l;
    }
    
    public static JTextField createTextField(int n){
        JTextField t = new RoundTextField(n);
        t.setFont(Style.FIELD_FONT);
        t.setBorder(Style.FIELD_MARGIN);
        return t;
    }
    
    public static JPasswordField createPasswordField(int n){
        JPasswordField p = new RoundPasswordField(n);
        p.setFont(Style.FIELD_FONT);
        p.setBorder(Style.FIELD_MARGIN);
        p.setEchoChar('\u25CF');
        return p;
    }
    
    public static JButton setButtonBorder(JButton b, int r){
        b.setBorder(new RoundBorderButton(r));
        b.setUI(new PolishRoundBoderButton());
        return b;
    }
    
    public static void initJOptionPane(){
        Toolkit.getDefaultToolkit().beep();
        UIManager.put("OptionPane.messageFont", Style.MONOSPACED_FONT);
    }
    
    public static JButton createHyperlinkButton(String label, int t,int l, int b, int r){
        JButton hyperlinkButton = new JButton(label);
        hyperlinkButton.setFont(Style.FIELD_FONT);
        hyperlinkButton.setForeground(Color.BLUE);
        hyperlinkButton.setContentAreaFilled(false);
        hyperlinkButton.setBorder(BorderFactory.createEmptyBorder(t, l, b, r));
        hyperlinkButton.addMouseListener(new HeaderMouseListener());
        return hyperlinkButton;
    }
    
    public static JLabel createTitleLabel(String label){
        JLabel headerLabel = new JLabel(label);
        headerLabel.setFont(Style.SUBHEADER_FONT);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return headerLabel;
    }
    
    public static JPanel createFormTitlePanel(String label){
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = createTitleLabel(label);
        titlePanel.setBackground(Style.BLUEGREEN);
        titlePanel.add(titleLabel);
        return titlePanel;
    }
    
    public static JScrollPane setFormTableProperties(JTable table){
        table.setRowHeight(25);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(220);
        columnModel.getColumn(1).setPreferredWidth(70);

        table.setFont(Style.BODY_FONT);
        table.getTableHeader().setFont(Style.BODY_FONT);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setBackground(Style.AQUA);
        table.getTableHeader().setBackground(Style.BLUEGREEN);
        table.setIntercellSpacing(new Dimension(20,1));
        table.setPreferredScrollableViewportSize(new Dimension(290, 150));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return tableScrollPane;
    }
    
    public static JTextArea createStaticTextArea(int lines, int characters, String text){
        JTextArea staticTextArea = new JTextArea(lines, characters);
        staticTextArea.setBackground(null);
        staticTextArea.setBorder(null);
        staticTextArea.setFont(Style.BODY_FONT);
        staticTextArea.setText(text);
        staticTextArea.setLineWrap(true);
        staticTextArea.setWrapStyleWord(true);
        staticTextArea.setEditable(false);
        return staticTextArea;
    }
}