package us.kosdt.harmonics;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class HarmonicsControlFrame {

    private SourceDataLine line;
    private boolean soundPlaying;
    private long samplesAdded;
    private byte[] currentWaveform;

    private JFrame topLevelFrame;
    private final HarmonicsVariables harmonicsVariables;

    private final int PRECISION = 1000;
    private final int SAMPLE_RATE = 44100;
    private final double LATENCY = SAMPLE_RATE / 10;
    
    private final AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
    
    public HarmonicsControlFrame() {
        harmonicsVariables = new HarmonicsVariables();
        topLevelFrame = new JFrame("Harmonics Control");
        initUI();
        initAudio();
        topLevelFrame.pack();
        topLevelFrame.setVisible(true);
    }

    private void initUI() {
        
        topLevelFrame.setLocationRelativeTo(null);
        topLevelFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container rootPanel = topLevelFrame.getContentPane();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));

        WaveformPanel waveformPanel = new WaveformPanel();
        waveformPanel.updateWaveform(harmonicsVariables);
        rootPanel.add(waveformPanel);

        JPanel totalAmplitudePanel = new JPanel();
        JLabel totalAmplitudeLabel = new JLabel("Total Amplitude");
        totalAmplitudePanel.add(totalAmplitudeLabel);
        JSlider totalAmplitudeSlider = new JSlider(0, PRECISION, (int)(harmonicsVariables.totalAmplitude*PRECISION));
        totalAmplitudeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                harmonicsVariables.totalAmplitude = totalAmplitudeSlider.getValue() / (double)PRECISION;
                currentWaveform = generateWaveform(harmonicsVariables);
                waveformPanel.updateWaveform(harmonicsVariables);
            }
        });
        totalAmplitudePanel.add(totalAmplitudeSlider);
        rootPanel.add(totalAmplitudePanel);

        JPanel harmonicAmplitudePanel = new JPanel();
        JLabel harmonicAmplitudeLabel = new JLabel("Harmonic Amplitude");
        harmonicAmplitudePanel.add(harmonicAmplitudeLabel);
        JSlider harmonicAmplitudeSlider = new JSlider(0, PRECISION, (int)(harmonicsVariables.harmonicAmplitude*PRECISION));
        harmonicAmplitudeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                harmonicsVariables.harmonicAmplitude = harmonicAmplitudeSlider.getValue() / (double)PRECISION;
                currentWaveform = generateWaveform(harmonicsVariables);
                waveformPanel.updateWaveform(harmonicsVariables);
            }
        });
        harmonicAmplitudePanel.add(harmonicAmplitudeSlider);
        rootPanel.add(harmonicAmplitudePanel);

        JPanel harmonicDropoffPanel = new JPanel();
        JLabel harmonicDropoffLabel = new JLabel("Harmonic Dropoff");
        harmonicDropoffPanel.add(harmonicDropoffLabel);
        JSlider harmonicDropoffSlider = new JSlider(-PRECISION, PRECISION, (int)(harmonicsVariables.harmonicDropoff*PRECISION));
        harmonicDropoffSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                harmonicsVariables.harmonicDropoff = harmonicDropoffSlider.getValue() / (double)PRECISION;
                currentWaveform = generateWaveform(harmonicsVariables);
                waveformPanel.updateWaveform(harmonicsVariables);
            }
        });
        harmonicDropoffPanel.add(harmonicDropoffSlider);
        rootPanel.add(harmonicDropoffPanel);

        JPanel numberOfHarmonicsPanel = new JPanel();
        JLabel numberOfHarmonicsLabel = new JLabel("Number of Harmonics");
        numberOfHarmonicsPanel.add(numberOfHarmonicsLabel);
        JSlider numberOfHarmonicsSlider = new JSlider(0, 10, harmonicsVariables.numberOfHarmonics);
        numberOfHarmonicsSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                harmonicsVariables.numberOfHarmonics = numberOfHarmonicsSlider.getValue();
                currentWaveform = generateWaveform(harmonicsVariables);
                waveformPanel.updateWaveform(harmonicsVariables);
            }
        });
        numberOfHarmonicsPanel.add(numberOfHarmonicsSlider);
        rootPanel.add(numberOfHarmonicsPanel);

        JPanel frequencyPanel = new JPanel();
        JLabel frequencyLabel = new JLabel("Frequency");
        frequencyPanel.add(frequencyLabel);
        JSlider frequencySlider = new JSlider(30*PRECISION, 500*PRECISION, (int)(harmonicsVariables.frequency*PRECISION));
        frequencySlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                harmonicsVariables.frequency = frequencySlider.getValue()/PRECISION;
                currentWaveform = generateWaveform(harmonicsVariables);
                waveformPanel.updateWaveform(harmonicsVariables);
            }
        });
        frequencyPanel.add(frequencySlider);
        rootPanel.add(frequencyPanel);

        JButton playButton = new JButton("Play");
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (soundPlaying) {
                    playButton.setText("Play");
                    stopWaveform();
                } else {
                    playButton.setText("Stop");
                    playWaveform();
                }
            }
        });
        rootPanel.add(playButton);
        
    }

    private void initAudio() {
        try {
            line = AudioSystem.getSourceDataLine(FORMAT);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        soundPlaying = false;
        currentWaveform = generateWaveform(harmonicsVariables);

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    int samples = (int)(SAMPLE_RATE/harmonicsVariables.frequency);
                    if ((samplesAdded - line.getLongFramePosition()) <= LATENCY) {
                        line.write(currentWaveform, 0, samples);
                        samplesAdded += samples;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    stopWaveform();
                    playWaveform();
                }
            }
        }, 0, 1);
    }

    private void playWaveform() {
        try {
            line.open(FORMAT);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        line.start();
        samplesAdded = 0L;
        soundPlaying = true;
    }

    private void stopWaveform() {
        soundPlaying = false;
        line.flush();
        line.close();
    }

    private byte doubleToByte(double d) {
        return (byte)(int)(d * 127);
    }

    private byte[] generateWaveform(HarmonicsVariables variables) {
        int samples = (int)(SAMPLE_RATE/variables.frequency);
        byte[] waveformBytes = new byte[samples];
        for (int x = 0; x < samples; x++) {
            double y = variables.totalAmplitude*HarmonicsFunctions.harmonicSum(variables, x, samples);
            waveformBytes[x] = doubleToByte(y);
        }
        return waveformBytes;
    }

}