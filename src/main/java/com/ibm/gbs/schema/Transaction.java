/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.ibm.gbs.schema;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class Transaction extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -7418046482850829973L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Transaction\",\"namespace\":\"com.ibm.gbs.schema\",\"fields\":[{\"name\":\"balanceId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"accountId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"balance\",\"type\":\"float\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<Transaction> ENCODER =
      new BinaryMessageEncoder<Transaction>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<Transaction> DECODER =
      new BinaryMessageDecoder<Transaction>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<Transaction> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<Transaction> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<Transaction> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<Transaction>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this Transaction to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a Transaction from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a Transaction instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static Transaction fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public java.lang.String balanceId;
  @Deprecated public java.lang.String accountId;
  @Deprecated public float balance;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public Transaction() {}

  /**
   * All-args constructor.
   * @param balanceId The new value for balanceId
   * @param accountId The new value for accountId
   * @param balance The new value for balance
   */
  public Transaction(java.lang.String balanceId, java.lang.String accountId, java.lang.Float balance) {
    this.balanceId = balanceId;
    this.accountId = accountId;
    this.balance = balance;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return balanceId;
    case 1: return accountId;
    case 2: return balance;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: balanceId = (java.lang.String)value$; break;
    case 1: accountId = (java.lang.String)value$; break;
    case 2: balance = (java.lang.Float)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'balanceId' field.
   * @return The value of the 'balanceId' field.
   */
  public java.lang.String getBalanceId() {
    return balanceId;
  }


  /**
   * Sets the value of the 'balanceId' field.
   * @param value the value to set.
   */
  public void setBalanceId(java.lang.String value) {
    this.balanceId = value;
  }

  /**
   * Gets the value of the 'accountId' field.
   * @return The value of the 'accountId' field.
   */
  public java.lang.String getAccountId() {
    return accountId;
  }


  /**
   * Sets the value of the 'accountId' field.
   * @param value the value to set.
   */
  public void setAccountId(java.lang.String value) {
    this.accountId = value;
  }

  /**
   * Gets the value of the 'balance' field.
   * @return The value of the 'balance' field.
   */
  public float getBalance() {
    return balance;
  }


  /**
   * Sets the value of the 'balance' field.
   * @param value the value to set.
   */
  public void setBalance(float value) {
    this.balance = value;
  }

  /**
   * Creates a new Transaction RecordBuilder.
   * @return A new Transaction RecordBuilder
   */
  public static com.ibm.gbs.schema.Transaction.Builder newBuilder() {
    return new com.ibm.gbs.schema.Transaction.Builder();
  }

  /**
   * Creates a new Transaction RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new Transaction RecordBuilder
   */
  public static com.ibm.gbs.schema.Transaction.Builder newBuilder(com.ibm.gbs.schema.Transaction.Builder other) {
    if (other == null) {
      return new com.ibm.gbs.schema.Transaction.Builder();
    } else {
      return new com.ibm.gbs.schema.Transaction.Builder(other);
    }
  }

  /**
   * Creates a new Transaction RecordBuilder by copying an existing Transaction instance.
   * @param other The existing instance to copy.
   * @return A new Transaction RecordBuilder
   */
  public static com.ibm.gbs.schema.Transaction.Builder newBuilder(com.ibm.gbs.schema.Transaction other) {
    if (other == null) {
      return new com.ibm.gbs.schema.Transaction.Builder();
    } else {
      return new com.ibm.gbs.schema.Transaction.Builder(other);
    }
  }

  /**
   * RecordBuilder for Transaction instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Transaction>
    implements org.apache.avro.data.RecordBuilder<Transaction> {

    private java.lang.String balanceId;
    private java.lang.String accountId;
    private float balance;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.ibm.gbs.schema.Transaction.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.balanceId)) {
        this.balanceId = data().deepCopy(fields()[0].schema(), other.balanceId);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.accountId)) {
        this.accountId = data().deepCopy(fields()[1].schema(), other.accountId);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.balance)) {
        this.balance = data().deepCopy(fields()[2].schema(), other.balance);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
    }

    /**
     * Creates a Builder by copying an existing Transaction instance
     * @param other The existing instance to copy.
     */
    private Builder(com.ibm.gbs.schema.Transaction other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.balanceId)) {
        this.balanceId = data().deepCopy(fields()[0].schema(), other.balanceId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.accountId)) {
        this.accountId = data().deepCopy(fields()[1].schema(), other.accountId);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.balance)) {
        this.balance = data().deepCopy(fields()[2].schema(), other.balance);
        fieldSetFlags()[2] = true;
      }
    }

    /**
      * Gets the value of the 'balanceId' field.
      * @return The value.
      */
    public java.lang.String getBalanceId() {
      return balanceId;
    }


    /**
      * Sets the value of the 'balanceId' field.
      * @param value The value of 'balanceId'.
      * @return This builder.
      */
    public com.ibm.gbs.schema.Transaction.Builder setBalanceId(java.lang.String value) {
      validate(fields()[0], value);
      this.balanceId = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'balanceId' field has been set.
      * @return True if the 'balanceId' field has been set, false otherwise.
      */
    public boolean hasBalanceId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'balanceId' field.
      * @return This builder.
      */
    public com.ibm.gbs.schema.Transaction.Builder clearBalanceId() {
      balanceId = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'accountId' field.
      * @return The value.
      */
    public java.lang.String getAccountId() {
      return accountId;
    }


    /**
      * Sets the value of the 'accountId' field.
      * @param value The value of 'accountId'.
      * @return This builder.
      */
    public com.ibm.gbs.schema.Transaction.Builder setAccountId(java.lang.String value) {
      validate(fields()[1], value);
      this.accountId = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'accountId' field has been set.
      * @return True if the 'accountId' field has been set, false otherwise.
      */
    public boolean hasAccountId() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'accountId' field.
      * @return This builder.
      */
    public com.ibm.gbs.schema.Transaction.Builder clearAccountId() {
      accountId = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'balance' field.
      * @return The value.
      */
    public float getBalance() {
      return balance;
    }


    /**
      * Sets the value of the 'balance' field.
      * @param value The value of 'balance'.
      * @return This builder.
      */
    public com.ibm.gbs.schema.Transaction.Builder setBalance(float value) {
      validate(fields()[2], value);
      this.balance = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'balance' field has been set.
      * @return True if the 'balance' field has been set, false otherwise.
      */
    public boolean hasBalance() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'balance' field.
      * @return This builder.
      */
    public com.ibm.gbs.schema.Transaction.Builder clearBalance() {
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Transaction build() {
      try {
        Transaction record = new Transaction();
        record.balanceId = fieldSetFlags()[0] ? this.balanceId : (java.lang.String) defaultValue(fields()[0]);
        record.accountId = fieldSetFlags()[1] ? this.accountId : (java.lang.String) defaultValue(fields()[1]);
        record.balance = fieldSetFlags()[2] ? this.balance : (java.lang.Float) defaultValue(fields()[2]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<Transaction>
    WRITER$ = (org.apache.avro.io.DatumWriter<Transaction>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<Transaction>
    READER$ = (org.apache.avro.io.DatumReader<Transaction>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.balanceId);

    out.writeString(this.accountId);

    out.writeFloat(this.balance);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.balanceId = in.readString();

      this.accountId = in.readString();

      this.balance = in.readFloat();

    } else {
      for (int i = 0; i < 3; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.balanceId = in.readString();
          break;

        case 1:
          this.accountId = in.readString();
          break;

        case 2:
          this.balance = in.readFloat();
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}









