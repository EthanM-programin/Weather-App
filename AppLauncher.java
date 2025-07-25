// Created 7/18/25
// Credit - Yt: TapTap

import javax.swing.*;

public class AppLauncher
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                // Displays weather app GUI
                new WeatherAppGUI().setVisible(true);

                // API Test
                // System.out.println(WeatherApp.getLocationData("Tokyo"));
                // System.out.println(WeatherApp.getCurrentTime());

            }

        });

    }

}
