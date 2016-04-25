package lt.ltrp.data;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PhoneBook {

    private static final int MaxContacts = 10;

    private PhoneContact[] contacts;
    private int contactCount;
    private int ownerNumber;

    public PhoneBook( int ownerNumber) {
        this(ownerNumber, MaxContacts);
    }

    public PhoneBook( int ownerNumber, int size) {
        this.ownerNumber = ownerNumber;
        this.contactCount = 0;
        this.contacts = new PhoneContact[size];
    }

    public PhoneBook(PhoneContact[] contacts, int contactCount, int ownerNumber) {
        this.contacts = contacts;
        this.contactCount = contactCount;
        this.ownerNumber = ownerNumber;
    }

    public int getOwnerNumber() {
        return ownerNumber;
    }

    public void remove(PhoneContact contact) {
        int index = getIndex(contact);
        if(index != -1) {
            for(int i = index; i < contactCount; i++) {
                contacts[ index ] = contacts[index + 1];
            }
            contacts[contactCount-1] = null;
            contactCount--;
        }
    }

    public boolean isFull() {
        return contactCount >= contacts.length;
    }


    public void addContact(PhoneContact contact) {
        if(contactCount < contacts.length) {
            contacts[contactCount++] = contact;
        }
    }

    public boolean contains(PhoneContact contact) {
        for(PhoneContact c : contacts) {
            if(c == contact) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(int phonenumber) {
        for(PhoneContact c : contacts) {
            if(c.getNumber() == phonenumber) {
                return true;
            }
        }
        return false;
    }

    public String getContactName(int phonenumber) {
        for(PhoneContact c : contacts) {
            if(c.getNumber() == phonenumber)
                return c.getName();
        }
        return null;
    }

    public PhoneContact[] getContacts() {
        return contacts;
    }



    private int getIndex(PhoneContact contact) {
        for(int i = 0; i < contactCount; i++)
            if(contacts[i] == contact)
                return i;
        return -1;
    }
}
