
package rentsystem;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicSpinnerUI;

/**
 *
 * @author Theresa De Ocampo
 */

public class SpinnerControl {
    public static class CustomLineBorder extends LineBorder {
        private double arcw, arch;

        public CustomLineBorder(Color color, int thickness, double arcw, double arch) {
            super(color, thickness);
            this.arcw = arcw;
            this.arch = arch;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            if ((thickness > 0) && (g instanceof Graphics2D)) {
                Graphics2D g2d = (Graphics2D) g;
                Color oldColor = g2d.getColor();
                g2d.setColor(lineColor);
                Path2D path = new Path2D.Double(Path2D.WIND_EVEN_ODD);
                path.append(new RoundRectangle2D.Double(x, y, width, height, thickness, thickness), false);
                path.append(new RoundRectangle2D.Double(x + thickness, y + thickness, width - 2 * thickness, height - 2 * thickness, arcw, arch), false);
                g2d.fill(path);
                g2d.setColor(oldColor);
            }
        }

        public void setArcWidth(double arcw) {
            this.arcw = arcw;
        }

        public void setArcHeight(double arch) {
            this.arch = arch;
        }

        public void setLineColor(Color lineColor) {
            this.lineColor = lineColor;
        }

        public double getArcWidth() {
            return arcw;
        }

        public double getArcHeight() {
            return arch;
        }
    }

    public static class CustomJButton extends JButton {
        private double arcw, arch;

        public CustomJButton(double arcw, double arch) {
            this.arcw = arcw;
            this.arch = arch;
            this.setPreferredSize(new Dimension(40, 30));
        }

        public void setArcWidth(double arcw) {
            this.arcw = arcw;
            repaint();
        }

        public void setArcHeight(double arch) {
            this.arch = arch;
            repaint();
        }

        public double getArcWidth() {
            return arcw;
        }

        public double getArcHeight() {
            return arch;
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension sz = super.getPreferredSize();
            sz.width = Math.max(sz.width, Math.round((float) getArcWidth()));
            sz.height = Math.max(sz.height, Math.round((float) getArcHeight()));
            return sz;
        }

        protected Shape createShape() {
            return new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), getArcWidth(), getArcHeight());
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setClip(createShape());
            super.paint(g2d);
            g2d.dispose();
        }

        @Override
        public void update(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setClip(createShape());
            super.update(g2d);
            g2d.dispose();
        }

        @Override
        public boolean contains(int x, int y) {
            return createShape().contains(x, y);
        }
    }

    public static class CustomJSpinnerLayout implements LayoutManager {
        private final int gap; 

        private Component nextButton;
        private Component previousButton;
        private Component editor;

        public CustomJSpinnerLayout(int gap) {
            this.gap = gap;
            nextButton = null;
            previousButton = null;
            editor = null;
        }

        @Override
        public void addLayoutComponent(String constraints, Component c) {
            switch (constraints) {
                case "Next": nextButton = c; break;
                case "Previous": previousButton = c; break;
                case "Editor": editor = c; break;
            }
        }

        @Override
        public void removeLayoutComponent(Component c) {
            if (c == nextButton)
                nextButton = null;
            else if (c == previousButton)
                previousButton = null;
            else if (c == editor)
                editor = null;
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return minimumLayoutSize(parent);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            Dimension next = nextButton.getPreferredSize();
            Dimension prev = previousButton.getPreferredSize();
            Dimension edit = editor.getPreferredSize();
            Insets pari = parent.getInsets();
            int totalHeight = Math.max(edit.height, next.height + prev.height + gap);
            int buttonMaxWidth = Math.max(next.width, prev.width);
            return new Dimension(buttonMaxWidth + edit.width + pari.left, totalHeight + pari.top + pari.bottom);
        }

        @Override
        public void layoutContainer(Container parent) {
            if (editor != null || nextButton != null || previousButton != null) {
                Dimension prnt = parent.getSize();
                Dimension next = nextButton.getPreferredSize();
                Dimension prev = previousButton.getPreferredSize();
                Insets i = parent.getInsets();
                int maxButtonWidth = Math.max(next.width, prev.width);
                int buttonHeight = Math.round((prnt.height - gap) / 2f);
                editor.setBounds(i.left, i.top, prnt.width - i.left - i.right - maxButtonWidth, prnt.height - i.top - i.bottom);
                nextButton.setBounds(prnt.width - maxButtonWidth, 0, maxButtonWidth, buttonHeight);
                previousButton.setBounds(prnt.width - maxButtonWidth, prnt.height - buttonHeight, maxButtonWidth, buttonHeight);
            }
        }
    }

    public static class CustomBasicSpinnerUI extends BasicSpinnerUI {
        @Override
        protected Component createPreviousButton() {
            if (spinner instanceof CustomJSpinner) {
                CustomJButton prev = ((CustomJSpinner) spinner).getButtonPrevious();
                prev.setInheritsPopupMenu(true); 
                prev.setName("Spinner.previousButton");
                installPreviousButtonListeners(prev);
                return prev;
            }
            return super.createPreviousButton();
        }

        @Override
        protected Component createNextButton() {
            if (spinner instanceof CustomJSpinner) {
                CustomJButton next = ((CustomJSpinner) spinner).getButtonNext();
                next.setInheritsPopupMenu(true); 
                next.setName("Spinner.nextButton"); 
                installNextButtonListeners(next);
                return next;
            }
            return super.createNextButton();
        }

        @Override
        protected LayoutManager createLayout() {
            return new CustomJSpinnerLayout(5);
        }
    }

    public static class CustomJSpinner extends JSpinner {
        private CustomJButton next, prev;
        private double arcw, arch;

        public CustomJSpinner(SpinnerModel model, double arcw, double arch) {
            super(model);
            this.arcw = arcw;
            this.arch = arch;
            next = new CustomJButton(10, 10);
            prev = new CustomJButton(10, 10);
        }

        public void setButtonPrevious(CustomJButton prev) {
            this.prev = prev;
            revalidate();
            repaint();
        }

        public void setButtonNext(CustomJButton next) {
            this.next = next;
            revalidate();
            repaint();
        }

        public CustomJButton getButtonPrevious() {
            return prev;
        }

        public CustomJButton getButtonNext() {
            return next;
        }

        public void setArcWidth(double arcw) {
            this.arcw = arcw;
            repaint();
        }

        public void setArcHeight(double arch) {
            this.arch = arch;
            repaint();
        }

        public double getArcWidth() {
            return arcw;
        }

        public double getArcHeight() {
            return arch;
        }

        protected Shape createShape() {
            return new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arcw, arch);
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setClip(createShape());
            Color old = g2d.getColor();
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(old);
            super.paint(g2d);
            g2d.dispose();
        }

        @Override
        public void update(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setClip(createShape());
            Color old = g2d.getColor();
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(old);
            super.update(g2d);
            g2d.dispose();
        }

        @Override
        public boolean contains(int x, int y) {
            return createShape().contains(x, y);
        }
    }

    public static void initCustomJButton(CustomJButton cjb, String text, Color nonRolloverBorderColor, Color rolloverBorderColor, int borderThickness) {
        cjb.setOpaque(false);
        cjb.setText(text);

        CustomLineBorder clb = new CustomLineBorder(nonRolloverBorderColor, borderThickness, cjb.getArcWidth(), cjb.getArcHeight());
        cjb.setBorder(clb);

        // Creates the mouse rollover effect of changing the color of the border of the button
        cjb.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mevt) {
                clb.setLineColor(rolloverBorderColor);
                cjb.repaint();
            }

            @Override
            public void mouseExited(MouseEvent mevt) {
                clb.setLineColor(nonRolloverBorderColor);
                cjb.repaint();
            }
        });
    }
    
    public static CustomJSpinner createCustomJSpinner(int width, int height){
        double arcw = 10, arch = 10;
        int borderThickness = 2;
        Color borderMainColor = Style.BLUEGREEN.darker(), buttonRolloverBorderColor = Style.BLUEGREEN;

        CustomJSpinner spin = new CustomJSpinner(new SpinnerNumberModel(), arcw, arch);

        // Customizing spinner
        spin.setUI(new CustomBasicSpinnerUI());
        spin.setOpaque(false);
        spin.setBorder(new CustomLineBorder(borderMainColor, borderThickness, spin.getArcWidth(), spin.getArcHeight())); 
        spin.setPreferredSize(new Dimension(width, height));
        spin.setMaximumSize(new Dimension(width, height));
        spin.setMinimumSize(new Dimension(width, height));
        spin.setBackground(Color.WHITE); 

        //Customizing spinner's control buttons:
        initCustomJButton(spin.getButtonNext(), "+", borderMainColor, buttonRolloverBorderColor, borderThickness);
        initCustomJButton(spin.getButtonPrevious(), "-", borderMainColor, buttonRolloverBorderColor, borderThickness);

        //Customizing spinner's field
        JComponent editor = spin.getEditor();
        editor.setOpaque(false); 
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField jftf = ((JSpinner.DefaultEditor) editor).getTextField();
            jftf.setOpaque(false);
            jftf.setHorizontalAlignment(JTextField.CENTER);
            jftf.setFont(Style.FIELD_FONT);
        }
        return spin;
    }
}