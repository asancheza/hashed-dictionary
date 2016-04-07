import java.lang.*; 
import java.util.*; 
import java.io.Serializable; 

public class HashedDictionary implements DictionaryInterface, Serializable {
  private TableEntry [] hashTable;          
  private int currentSize;                      
  private static final int DEFAULT_SIZE = 101; 
  private static final double MAX_LOAD_FACTOR = 0.5;  

  public HashedDictionary() {
    hashTable = new TableEntry[DEFAULT_SIZE];
    currentSize = 0;
  } 

  public HashedDictionary(int tableSize) {
    int primeSize = getNextPrime(tableSize);
    
    hashTable = new TableEntry[primeSize];
    currentSize = 0;
  }
  
  public Object add(Object key, Object value) {
    Object oldValue;
    
    if (isHashTableTooFull())
      rehash();

    int index = getHashIndex(key);
    index = probe(index, key); // check for and resolve collision
    
    if (hashTable[index] == null || hashTable[index].isRemoved()) {
      hashTable[index] = new TableEntry(key, value);
      currentSize++;
      oldValue = null;
    } else {
      oldValue = hashTable[index].getValue(); // get old value for return
      hashTable[index].setValue(value);      // replace value
    }

    return oldValue;
  }

  public Object remove(Object key) {
    Object result = null;
    
    int index = getHashIndex(key);

    index = probe(index, key);
    
    if ((hashTable[index] != null) && (hashTable[index].isIn())) { 
      hashTable[index].setToRemoved();
      result = hashTable[index].getValue();
      currentSize--;
    }
    
    return result;
  }

  public Object getValue(Object key)
  {
    Object result = null;
    
    int index = getHashIndex(key);

    index = probe(index, key);

    if ((hashTable[index] != null) && (hashTable[index].isIn()))
    {
      // key found; get value
      result = hashTable[index].getValue(); 
    }
    
    return result;
   }

  private int probe(int index, Object key) {
    boolean found = false;
    boolean repeatedIndex = false;

    // loop must ignore removed entries (ie, not null but not occupied)
    // loop ends at key found or null
    // index always in range due to mod of probe seq
    int increment = 1; // for quadratic probing
    
    int firstIndex = index;
    int notInUseIndex = -1; // index of first location not occupied, but not null
    while ( !found && !repeatedIndex && (hashTable[index] != null) )
    {
      // skip entries that are not used [were removed]
      if ( hashTable[index].isRemoved() )
      {
        // save index to first not-in-use location found
        if (notInUseIndex == -1)
          notInUseIndex = index;// want the first one to keep it close to probe seq
        index = (index + 1) % hashTable.length;       // linear probing
      }
      else if (key.equals(hashTable[index].getKey()) )
        found = true; // key found
      
      else // follow probe sequence
        index = (index + 1) % hashTable.length;       // linear probing

      if (index == firstIndex)
        repeatedIndex = true; // probe sequence is repeating
    } // end while

    if (found || (notInUseIndex == -1) ) 
      return index;
    else 
      return notInUseIndex;
  }
  
  public boolean contains(Object key) {
    return getValue(key) != null; 
  } 

  public boolean isEmpty() {
    return currentSize == 0;
  }
  
  public boolean isFull() {
    return false;
  }

  public int getSize() {
    return currentSize;
  }

  public final void clear() { 
    currentSize = 0;
  }

  private int getHashIndex(Object key) {
    int hashIndex = key.hashCode() % hashTable.length; 
    
    if (hashIndex < 0) {
      hashIndex = hashIndex + hashTable.length;
    } 

    return hashIndex;
  }
  
  private void rehash() { 
     TableEntry[] oldTable = hashTable; 
     int oldSize = hashTable.length;     
     int newSize = getNextPrime(oldSize + oldSize);
     
     hashTable = new TableEntry[newSize];    // increase size of array
     
     currentSize = 0; 
     
     // rehash dictionary entries from old array to new, bigger array.
     // skip null locations and not-in-use entries
     for (int index = 0; index < oldSize; ++index) {
      if ( (oldTable[index] != null) && oldTable[index].isIn() ) {
        add(oldTable[index].getKey(), oldTable[index].getValue());
      }
     } 
  }

  private boolean isHashTableTooFull() {
      return currentSize >= MAX_LOAD_FACTOR * hashTable.length;
  }

  private int getNextPrime(int integer) {
    // if even, add 1 to make odd
    if (integer % 2 == 0)
      integer++;

    // test odd integers
    while(!isPrime(integer))
      integer = integer + 2;

    return integer;
  }
  
  private boolean isPrime(int integer) {
    boolean result;
    boolean done = false;
    
    // 2 and 3 are prime
    if ( (integer == 2) || (integer == 3) )
    {
      result = true;
    }
    
    // 1 and even numbers are not prime
    else if ( (integer == 1) || (integer % 2 == 0) )
      {
      result = false; 
    }
    
    // Assertion: integer is odd and >= 5.
    else {
      // a prime is odd and not divisible by every odd integer up to its square root
      result = true; // assume prime
      for (int divisor = 3; !done && (divisor * divisor <= integer); divisor = divisor + 2)
      {
        if (integer % divisor == 0)
          {
          result = false; // divisible; not prime
          done = true;
        } // end if
      } // end for
    } // end if
      
    return result;
  }

  public Iterator getKeyIterator() { 
    return new KeyIterator();
  } 
  
  public Iterator getValueIterator() { 
    return new ValueIterator();
  }

  private class KeyIterator implements Iterator {
    private int currentIndex;
    private int numberLeft; 
    
    private KeyIterator() {
      currentIndex = 0;
      numberLeft = currentSize; //ok
    } 
    
    public boolean hasNext() {
      return numberLeft > 0; 
    }
    
    public Object next() {
      Object result = null;
      
      if (hasNext())
      {
        while ( (hashTable[currentIndex] == null) || hashTable[currentIndex].isRemoved() ) 
        {
          currentIndex++;
        } // end while
        
        result = hashTable[currentIndex].getKey();
        numberLeft--;
        currentIndex++;
      } else {
        throw new NoSuchElementException();
      }
    
      return result;
    }
    
    public void remove() {
      //throw new UnsupportedOperationException();
    }
  }
  
  private class ValueIterator implements Iterator {
    private int currentIndex;
    private int numberLeft; 
    
    private ValueIterator() 
    {
      currentIndex = 0;
      numberLeft = currentSize;
    } // end default constructor
    
    public boolean hasNext() 
    {
      return numberLeft > 0; 
    } // end hasNext
    
    public Object next()
    {
      Object result = null;
      
      if (hasNext())
      {
        while ( (currentIndex < hashTable.length) && 
                ( (hashTable[currentIndex] == null) || hashTable[currentIndex].isRemoved() ) )
        {
          currentIndex++;
        } // end while
        
        result = hashTable[currentIndex].getValue();
        numberLeft--;
        currentIndex++;
      }
      else
      {
        throw new NoSuchElementException();
      }
    
      return result;
    } // end next
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }

  private class TableEntry implements java.io.Serializable {
    private Object entryKey;
    private Object entryValue;
    private boolean inTable; // true if entry is in table, false if entry was removed
    
    private TableEntry(Object key, Object value) {
      entryKey = key;
      entryValue = value;
      inTable = true;
    }
    
    public Object getKey() {
      return entryKey;
    }
    
    public Object getValue() {
      return entryValue;
    }
    
    public void setValue(Object newValue) {
      entryValue = newValue;
    }

    public boolean isIn() {
      return inTable;
    }
    
    public boolean isRemoved() // opposite of contains {
      return !inTable;
    }

    public void setToRemoved() {
      inTable = false;
    }
    
    public void setToIn() {
      inTable = true;
    }
    
    public boolean equals(Object other)
    {
      return inTable && ((TableEntry)other).entryKey.equals(this.entryKey) && 
                        ((TableEntry)other).entryValue.equals(this.entryValue);
                        
    }
  }
}