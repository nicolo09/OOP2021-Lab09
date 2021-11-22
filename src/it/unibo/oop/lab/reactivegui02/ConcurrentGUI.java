package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

//import it.unibo.oop.lab.reactivegui01.ConcurrentGUI.Agent;

public class ConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;

    private volatile int counter;

    private final static int WAIT_TIME = 100;

    private final JLabel display;

    ConcurrentGUI() {

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        display = new JLabel(Integer.toString(counter));
        final JButton up = new JButton("up");
        final JButton down = new JButton("down");
        final JButton stop = new JButton("stop");
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        // Multi thread counter
        final Agent agent = new Agent();
        new Thread(agent).start();

        /*
         * Register a listener that stops it
         */
        stop.addActionListener(new ActionListener() {
            /**
             * event handler associated to action event on button stop.
             * 
             * @param e
             *              the action event that will be handled by this listener
             */
            @Override
            public void actionPerformed(final ActionEvent e) {
                // Agent should be final
                agent.stopCounting();
                up.setEnabled(false);
                down.setEnabled(false);
                stop.setEnabled(false);
            }
        });

        /*
         * Register a listener that change counting to down
         */
        down.addActionListener(new ActionListener() {
            /**
             * event handler associated to action event on button down.
             * 
             * @param e
             *              the action event that will be handled by this listener
             */
            @Override
            public void actionPerformed(final ActionEvent e) {
                // Agent should be final
                agent.countDownward();
            }
        });

        /*
         * Register a listener that change counting to up
         */
        up.addActionListener(new ActionListener() {
            /**
             * event handler associated to action event on button up.
             * 
             * @param e
             *              the action event that will be handled by this listener
             */
            @Override
            public void actionPerformed(final ActionEvent e) {
                // Agent should be final
                agent.countUpward();
            }
        });
    }

    public class Agent implements Runnable {
        private boolean down;
        private boolean stop;

        @Override
        public void run() {
            while (!stop) {
                try {
                    // Increment counter
                    if (down) {
                        counter--;
                    } else {
                        counter++;
                    }
                    // Tell EDT to update view
                    SwingUtilities.invokeAndWait(new Runnable() {

                        @Override
                        public void run() {
                            display.setText(Integer.toString(counter));
                        }

                    });
                    // Wait prefixed time
                    Thread.sleep(WAIT_TIME);
                } catch (InterruptedException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        }

        public void countDownward() {
            down = true;
        }

        public void countUpward() {
            down = false;
        }

        public void stopCounting() {
            stop = true;
        }

    }
}
