// Demonstration of OOP Concepts in Java
// Concepts covered: Encapsulation, Inheritance, Polymorphism, Abstraction

// --------------------
// ABSTRACTION
// --------------------
abstract class Animal {
    private String name;  // Encapsulation

    public Animal(String name) {
        this.name = name;
    }

    public String getName() {   // Getter (Encapsulation)
        return name;
    }

    public void setName(String name) {  // Setter (Encapsulation)
        this.name = name;
    }

    // Abstract method (must be implemented by subclasses)
    public abstract void makeSound();

    public void eat() {
        System.out.println(name + " is eating.");
    }
}

// --------------------
// INHERITANCE
// --------------------
class Dog extends Animal {

    public Dog(String name) {
        super(name);
    }

    // POLYMORPHISM (Method Overriding)
    @Override
    public void makeSound() {
        System.out.println(getName() + " says: Woof!");
    }
}

class Cat extends Animal {

    public Cat(String name) {
        super(name);
    }

    // POLYMORPHISM (Method Overriding)
    @Override
    public void makeSound() {
        System.out.println(getName() + " says: Meow!");
    }
}

// --------------------
// MAIN CLASS
// --------------------
public class OOPDemo {

    public static void main(String[] args) {

        // Polymorphism (Parent reference, Child object)
        Animal dog = new Dog("Buddy");
        Animal cat = new Cat("Whiskers");

        dog.makeSound();
        cat.makeSound();

        dog.eat();
        cat.eat();

        System.out.println("---- Changing name using Encapsulation ----");

        dog.setName("Rocky");
        dog.makeSound();
    }
}
