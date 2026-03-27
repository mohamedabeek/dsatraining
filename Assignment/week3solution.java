// Abstract Base Class (Abstraction)
abstract class Device {
    private String deviceName;   // Encapsulation
    private boolean powerStatus;

    // Constructor
    public Device(String deviceName) {
        this.deviceName = deviceName;
        this.powerStatus = false;
    }

    // Getters & Setters (Encapsulation)
    public String getDeviceName() {
        return deviceName;
    }

    public boolean getPowerStatus() {
        return powerStatus;
    }

    public void setPowerStatus(boolean status) {
        this.powerStatus = status;
    }

    // Common methods
    public void turnOn() {
        setPowerStatus(true);
        System.out.println(deviceName + " is turned ON");
    }

    public void turnOff() {
        setPowerStatus(false);
        System.out.println(deviceName + " is turned OFF");
    }

    // Abstract method (Polymorphism)
    public abstract void displayStatus();
}

// Derived Class 1 (Inheritance)
class Light extends Device {
    public Light(String name) {
        super(name);
    }

    // Polymorphism
    @Override
    public void displayStatus() {
        System.out.println("Light [" + getDeviceName() + "] is " +
                (getPowerStatus() ? "ON 💡" : "OFF"));
    }
}

// Derived Class 2 (Inheritance)
class Thermostat extends Device {
    private int temperature;

    public Thermostat(String name, int temperature) {
        super(name);
        this.temperature = temperature;
    }

    @Override
    public void displayStatus() {
        System.out.println("Thermostat [" + getDeviceName() + "] is " +
                (getPowerStatus() ? "ON 🌡️" : "OFF") +
                " | Temperature: " + temperature + "°C");
    }
}
// Interface (Abstraction)
interface PaymentMethod {
    void processPayment(double amount);
}

// Concrete Implementations (OCP)
class CreditCardPayment implements PaymentMethod {
    public void processPayment(double amount) {
        System.out.println("Paid ₹" + amount + " using Credit Card 💳");
    }
}

class PayPalPayment implements PaymentMethod {
    public void processPayment(double amount) {
        System.out.println("Paid ₹" + amount + " using PayPal 🌐");
    }
}

class UPIPayment implements PaymentMethod {
    public void processPayment(double amount) {
        System.out.println("Paid ₹" + amount + " using UPI 📱");
    }
}

// Service Class (DIP)
class PaymentProcessor {
    private PaymentMethod paymentMethod;

    // Dependency Injection
    public PaymentProcessor(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void process(double amount) {
        paymentMethod.processPayment(amount);
    }
}
// Small Interfaces (ISP)
interface EmailSender {
    void sendEmail(String message);
}

interface SMSSender {
    void sendSMS(String message);
}

interface PushNotificationSender {
    void sendPushNotification(String message);
}

// SRP: Each class has one responsibility

class EmailNotification implements EmailSender {
    public void sendEmail(String message) {
        System.out.println("Sending Email 📧: " + message);
    }
}

class SMSNotification implements SMSSender {
    public void sendSMS(String message) {
        System.out.println("Sending SMS 📱: " + message);
    }
}

class MobileAppNotification implements PushNotificationSender {
    public void sendPushNotification(String message) {
        System.out.println("Sending Push Notification 🔔: " + message);
    }
}
public class week3solution {
    public static void main(String[] args) {
        Device light = new Light("Living Room Light");
        Device thermostat = new Thermostat("Hall Thermostat", 24);

        light.turnOn();
        thermostat.turnOn();

        light.displayStatus();
        thermostat.displayStatus();

        light.turnOff();
        thermostat.turnOff();

        light.displayStatus();
        thermostat.displayStatus();

         PaymentMethod credit = new CreditCardPayment();
        PaymentMethod paypal = new PayPalPayment();
        PaymentMethod upi = new UPIPayment();

        PaymentProcessor processor1 = new PaymentProcessor(credit);
        processor1.process(1000);

        PaymentProcessor processor2 = new PaymentProcessor(paypal);
        processor2.process(2000);

        PaymentProcessor processor3 = new PaymentProcessor(upi);
        processor3.process(500);

         EmailSender email = new EmailNotification();
        SMSSender sms = new SMSNotification();
        PushNotificationSender push = new MobileAppNotification();

        email.sendEmail("Welcome to our service!");
        sms.sendSMS("Your OTP is 1234");
        push.sendPushNotification("You have a new alert!"); 
    }
}