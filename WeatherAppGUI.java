import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGUI extends JFrame
{

    private JSONObject weatherData;

    public WeatherAppGUI()
    {
        // Set GUI title
        super("Weather App");
        // Close program once exited
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Set GUI size
        setSize(450, 650);
        // Setting GUI to center
        setLocationRelativeTo(null);
        // Cancel layout manager
        setLayout(null);
        // Prevent resizing
        setResizable(false);

        addGuiComponents();

    }

    private void addGuiComponents()
    {
        // Search field
        JTextField searchTextField = new JTextField();
        // Location and size
        searchTextField.setBounds(15, 15, 351, 45);
        // Font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchTextField);

        // Weather images
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/weatherapp_images/cloudy.png"));
        // Location and size
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // Temperature text
        JLabel temperatureText = new JLabel("10 C");
        // Location and size
        temperatureText.setBounds(0, 350, 450, 54);
        // Font style and size
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
        // Center text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // Condition description
        JLabel conditionText = new JLabel("Cloudy");
        // Location and size
        conditionText.setBounds(0, 405, 450, 36);
        // Font style and size
        conditionText.setFont(new Font("Dialog", Font.PLAIN, 32));
        // Center text
        conditionText.setHorizontalAlignment(SwingConstants.CENTER);
        add(conditionText);

        // Humidity image
        JLabel humidityImage = new JLabel(loadImage("src/assets/weatherapp_images/humidity.png"));
        // Location and size
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // Humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        // Location and size
        humidityText.setBounds(90, 500, 85, 55);
        // Font style and size
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // Wind speed image
        JLabel windImage = new JLabel(loadImage("src/assets/weatherapp_images/windspeed.png"));
        // Location and size
        windImage.setBounds(220, 500, 74, 66);
        add(windImage);

        // Wind speed text
        JLabel windText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        // Location and size
        windText.setBounds(310, 500, 85, 55);
        // Font style and size
        windText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windText);

        // Search button
        JButton searchButton = new JButton(loadImage("src/assets/weatherapp_images/search.png"));
        // Change cursor to hand over button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Location and size
        searchButton.setBounds(375, 15, 47, 45);

        searchButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Get location from user
                String userInput = searchTextField.getText();
                // Validate input - remove whitespace to non-empty text
                if(userInput.replaceAll("\\s", "").length() <= 0)
                {
                    return;
                }

                // Retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                // Update GUI
                // Update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");

                // Depending on condition, updates to the weather image corresponds with condition
                switch(weatherCondition)
                {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/weatherapp_images/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/weatherapp_images/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/weatherapp_images/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/weatherapp_images/snow.png"));
                        break;
                }

                // Update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");
                // Update weather condition text
                conditionText.setText(weatherCondition);
                // Update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");
                // Update windspeed text
                double windspeed = (double) weatherData.get("windspeed");
                windText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");


            }
        });

        add(searchButton);

    }

    // Used to load images in GUI components
    private ImageIcon loadImage(String resourcePath)
    {
        try
        {
            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("Couldn't load image");
        return null;

    }

}