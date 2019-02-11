package us.kosdt.harmonics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class WaveformPanel extends JPanel {

    private static final long serialVersionUID = 1361694901877629507L;
    private static final int WAVEFORM_IMAGE_WIDTH = 600;
    private static final int WAVEFORM_IMAGE_HEIGHT = 400;
    private static final int WAVEFORM_IMAGE_PERIOD = 500;

    private static final int WAVEFORM_IMAGE_OFFSET = (WAVEFORM_IMAGE_WIDTH - WAVEFORM_IMAGE_PERIOD) / 2;

    private static final Color WAVEFORM_BACKGROUND = Color.BLACK;
    private static final Color WAVEFORM_STROKE = Color.WHITE;

    private BufferedImage waveform;

    public WaveformPanel() {
        waveform = new BufferedImage(WAVEFORM_IMAGE_WIDTH, WAVEFORM_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        setPreferredSize(new Dimension(WAVEFORM_IMAGE_WIDTH, WAVEFORM_IMAGE_HEIGHT));
        setBackground(WAVEFORM_BACKGROUND);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(waveform, 0, 0, this);
    }

    public void updateWaveform(HarmonicsVariables variables) {
        
        Graphics g = waveform.getGraphics();

        g.setColor(WAVEFORM_BACKGROUND);
        g.fillRect(0, 0, WAVEFORM_IMAGE_WIDTH, WAVEFORM_IMAGE_HEIGHT);
        
        g.setColor(WAVEFORM_STROKE);

        int prevX = -1;
        int prevY = (int)(variables.totalAmplitude*HarmonicsFunctions.harmonicSum(variables, realX(prevX), WAVEFORM_IMAGE_PERIOD)*WAVEFORM_IMAGE_HEIGHT/2);
        for (int x = 0; x < WAVEFORM_IMAGE_WIDTH; x++) {
            int y = (int)(variables.totalAmplitude*HarmonicsFunctions.harmonicSum(variables, realX(x), WAVEFORM_IMAGE_PERIOD)*WAVEFORM_IMAGE_HEIGHT/2);
            g.drawLine(prevX, screenSpaceY(prevY), x, screenSpaceY(y));
            prevX = x;
            prevY = y;
        }
        repaint();
    }

    private int realX(int screenSpaceX) {
        return screenSpaceX-WAVEFORM_IMAGE_OFFSET;
    }

    private int screenSpaceY(int realY) {
        return realY+WAVEFORM_IMAGE_HEIGHT/2;
    }

}