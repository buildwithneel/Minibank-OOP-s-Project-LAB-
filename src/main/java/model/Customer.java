package model;
import java.io.Serializable;
public class Customer implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;
    private static long customerCounter = 101;
    private String name;
    private String email;
    private String mobile;
    private final String customerId;
    private Address address;
    public Customer(String name, String email, String mobile) {
        this(name, email, mobile, null);
    }
    public Customer(String name, String email, String mobile, Address address) {
        this.customerId = generateCustomerId();
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
    }
    private static String generateCustomerId() {
        return "CUST" + customerCounter++;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getMobile() {
        return mobile;
    }
    public String getCustomerId() {
        return customerId;
    }
    public Address getAddress() {
        return address;
    }
    @Override
    public Customer clone() {
        try {
            Customer copy = (Customer) super.clone();
            if (address != null) copy.address = address.clone();
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
    public static class Address implements Cloneable, Serializable {
        private static final long serialVersionUID = 1L;
        private String line;
        private String city;
        private String pincode;
        public Address(String line, String city, String pincode) {
            this.line = line;
            this.city = city;
            this.pincode = pincode;
        }
        public String getLine() {
            return line;
        }
        public String getCity() {
            return city;
        }
        public String getPincode() {
            return pincode;
        }
        @Override
        public Address clone() {
            try {
                return (Address) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }
    }
}
