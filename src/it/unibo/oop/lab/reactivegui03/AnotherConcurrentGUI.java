package it.unibo.oop.lab.reactivegui03;

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
/**
 * Models a counter that goes upward or downward and stops after 10 seconds.
 * 
 */
public class AnotherConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;

    private int counter;

    private static final int WAIT_TIME = 100;

    private final JLabel display;
    private final JButton stop;
    private final JButton down;
    private final JButton up;

    AnotherConcurrentGUI() {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        display = new JLabel(Integer.toString(counter));
        up = new JButton("up");
        down = new JButton("down");
        stop = new JButton("stop");
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        // Multi thread counter
        final Agent agent = new Agent();
        final Agent2 stopperAgent = new Agent2();
        new Thread(agent).start();
        new Thread(stopperAgent).start();

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

    /**
     * Models a thread which manage a counter.
     *
     */
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

        /**
         * Changes the counter to downward. 
         */
        public void countDownward() {
            down = true;
        }

        /**
         * Changes the counter to upward. 
         */
        public void countUpward() {
            down = false;
        }

        /**
         * Stops the counter and disables the buttons.
         */
        public void stopCounting() {
            stop = true;
        }

    }

    /**
     * Stops the counter after 10 seconds.
     *
     */
    public class Agent2 implements Runnable {

        private static final long TIME_TO_STOP = 10_000;

        @Override
        public void run() {
            try {
                Thread.sleep(TIME_TO_STOP);
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        stop.doClick();
                    }

                });
            } catch (InterruptedException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }
}
