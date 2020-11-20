/**
 * Autogenerated by Avro
 *
 * <p>DO NOT EDIT DIRECTLY
 */
package org.icgc_argo.workflow_graph_lib.schema;

import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.SchemaStore;
import org.apache.avro.specific.SpecificData;

@org.apache.avro.specific.AvroGenerated
public class GraphEvent extends org.apache.avro.specific.SpecificRecordBase
    implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -7497127863358708963L;
  public static final org.apache.avro.Schema SCHEMA$ =
      new org.apache.avro.Schema.Parser()
          .parse(
              "{\"type\":\"record\",\"name\":\"GraphEvent\",\"namespace\":\"org.icgc_argo.workflow_graph_lib.schema\",\"fields\":[{\"name\":\"id\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"logicalType\":\"UUID\"},{\"name\":\"analysisId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"analysisState\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"analysisType\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"studyId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"experimentalStrategy\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"null\"]},{\"name\":\"donorIds\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}},{\"name\":\"files\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"AnalysisFile\",\"fields\":[{\"name\":\"dataType\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}}}]}");

  public static org.apache.avro.Schema getClassSchema() {
    return SCHEMA$;
  }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<GraphEvent> ENCODER =
      new BinaryMessageEncoder<GraphEvent>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<GraphEvent> DECODER =
      new BinaryMessageDecoder<GraphEvent>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   *
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<GraphEvent> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   *
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<GraphEvent> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link
   * SchemaStore}.
   *
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<GraphEvent> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<GraphEvent>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this GraphEvent to a ByteBuffer.
   *
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a GraphEvent from a ByteBuffer.
   *
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a GraphEvent instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of
   *     this class
   */
  public static GraphEvent fromByteBuffer(java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private java.lang.String id;
  private java.lang.String analysisId;
  private java.lang.String analysisState;
  private java.lang.String analysisType;
  private java.lang.String studyId;
  private java.lang.String experimentalStrategy;
  private java.util.List<java.lang.String> donorIds;
  private java.util.List<org.icgc_argo.workflow_graph_lib.schema.AnalysisFile> files;

  /**
   * Default constructor. Note that this does not initialize fields to their default values from the
   * schema. If that is desired then one should use <code>newBuilder()</code>.
   */
  public GraphEvent() {}

  /**
   * All-args constructor.
   *
   * @param id The new value for id
   * @param analysisId The new value for analysisId
   * @param analysisState The new value for analysisState
   * @param analysisType The new value for analysisType
   * @param studyId The new value for studyId
   * @param experimentalStrategy The new value for experimentalStrategy
   * @param donorIds The new value for donorIds
   * @param files The new value for files
   */
  public GraphEvent(
      java.lang.String id,
      java.lang.String analysisId,
      java.lang.String analysisState,
      java.lang.String analysisType,
      java.lang.String studyId,
      java.lang.String experimentalStrategy,
      java.util.List<java.lang.String> donorIds,
      java.util.List<org.icgc_argo.workflow_graph_lib.schema.AnalysisFile> files) {
    this.id = id;
    this.analysisId = analysisId;
    this.analysisState = analysisState;
    this.analysisType = analysisType;
    this.studyId = studyId;
    this.experimentalStrategy = experimentalStrategy;
    this.donorIds = donorIds;
    this.files = files;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() {
    return MODEL$;
  }

  public org.apache.avro.Schema getSchema() {
    return SCHEMA$;
  }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
      case 0:
        return id;
      case 1:
        return analysisId;
      case 2:
        return analysisState;
      case 3:
        return analysisType;
      case 4:
        return studyId;
      case 5:
        return experimentalStrategy;
      case 6:
        return donorIds;
      case 7:
        return files;
      default:
        throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value = "unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
      case 0:
        id = value$ != null ? value$.toString() : null;
        break;
      case 1:
        analysisId = value$ != null ? value$.toString() : null;
        break;
      case 2:
        analysisState = value$ != null ? value$.toString() : null;
        break;
      case 3:
        analysisType = value$ != null ? value$.toString() : null;
        break;
      case 4:
        studyId = value$ != null ? value$.toString() : null;
        break;
      case 5:
        experimentalStrategy = value$ != null ? value$.toString() : null;
        break;
      case 6:
        donorIds = (java.util.List<java.lang.String>) value$;
        break;
      case 7:
        files = (java.util.List<org.icgc_argo.workflow_graph_lib.schema.AnalysisFile>) value$;
        break;
      default:
        throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'id' field.
   *
   * @return The value of the 'id' field.
   */
  public java.lang.String getId() {
    return id;
  }

  /**
   * Sets the value of the 'id' field.
   *
   * @param value the value to set.
   */
  public void setId(java.lang.String value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'analysisId' field.
   *
   * @return The value of the 'analysisId' field.
   */
  public java.lang.String getAnalysisId() {
    return analysisId;
  }

  /**
   * Sets the value of the 'analysisId' field.
   *
   * @param value the value to set.
   */
  public void setAnalysisId(java.lang.String value) {
    this.analysisId = value;
  }

  /**
   * Gets the value of the 'analysisState' field.
   *
   * @return The value of the 'analysisState' field.
   */
  public java.lang.String getAnalysisState() {
    return analysisState;
  }

  /**
   * Sets the value of the 'analysisState' field.
   *
   * @param value the value to set.
   */
  public void setAnalysisState(java.lang.String value) {
    this.analysisState = value;
  }

  /**
   * Gets the value of the 'analysisType' field.
   *
   * @return The value of the 'analysisType' field.
   */
  public java.lang.String getAnalysisType() {
    return analysisType;
  }

  /**
   * Sets the value of the 'analysisType' field.
   *
   * @param value the value to set.
   */
  public void setAnalysisType(java.lang.String value) {
    this.analysisType = value;
  }

  /**
   * Gets the value of the 'studyId' field.
   *
   * @return The value of the 'studyId' field.
   */
  public java.lang.String getStudyId() {
    return studyId;
  }

  /**
   * Sets the value of the 'studyId' field.
   *
   * @param value the value to set.
   */
  public void setStudyId(java.lang.String value) {
    this.studyId = value;
  }

  /**
   * Gets the value of the 'experimentalStrategy' field.
   *
   * @return The value of the 'experimentalStrategy' field.
   */
  public java.lang.String getExperimentalStrategy() {
    return experimentalStrategy;
  }

  /**
   * Sets the value of the 'experimentalStrategy' field.
   *
   * @param value the value to set.
   */
  public void setExperimentalStrategy(java.lang.String value) {
    this.experimentalStrategy = value;
  }

  /**
   * Gets the value of the 'donorIds' field.
   *
   * @return The value of the 'donorIds' field.
   */
  public java.util.List<java.lang.String> getDonorIds() {
    return donorIds;
  }

  /**
   * Sets the value of the 'donorIds' field.
   *
   * @param value the value to set.
   */
  public void setDonorIds(java.util.List<java.lang.String> value) {
    this.donorIds = value;
  }

  /**
   * Gets the value of the 'files' field.
   *
   * @return The value of the 'files' field.
   */
  public java.util.List<org.icgc_argo.workflow_graph_lib.schema.AnalysisFile> getFiles() {
    return files;
  }

  /**
   * Sets the value of the 'files' field.
   *
   * @param value the value to set.
   */
  public void setFiles(java.util.List<org.icgc_argo.workflow_graph_lib.schema.AnalysisFile> value) {
    this.files = value;
  }

  /**
   * Creates a new GraphEvent RecordBuilder.
   *
   * @return A new GraphEvent RecordBuilder
   */
  public static org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder newBuilder() {
    return new org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder();
  }

  /**
   * Creates a new GraphEvent RecordBuilder by copying an existing Builder.
   *
   * @param other The existing builder to copy.
   * @return A new GraphEvent RecordBuilder
   */
  public static org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder newBuilder(
      org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder other) {
    if (other == null) {
      return new org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder();
    } else {
      return new org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder(other);
    }
  }

  /**
   * Creates a new GraphEvent RecordBuilder by copying an existing GraphEvent instance.
   *
   * @param other The existing instance to copy.
   * @return A new GraphEvent RecordBuilder
   */
  public static org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder newBuilder(
      org.icgc_argo.workflow_graph_lib.schema.GraphEvent other) {
    if (other == null) {
      return new org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder();
    } else {
      return new org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder(other);
    }
  }

  /** RecordBuilder for GraphEvent instances. */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<GraphEvent>
      implements org.apache.avro.data.RecordBuilder<GraphEvent> {

    private java.lang.String id;
    private java.lang.String analysisId;
    private java.lang.String analysisState;
    private java.lang.String analysisType;
    private java.lang.String studyId;
    private java.lang.String experimentalStrategy;
    private java.util.List<java.lang.String> donorIds;
    private java.util.List<org.icgc_argo.workflow_graph_lib.schema.AnalysisFile> files;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     *
     * @param other The existing Builder to copy.
     */
    private Builder(org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.analysisId)) {
        this.analysisId = data().deepCopy(fields()[1].schema(), other.analysisId);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.analysisState)) {
        this.analysisState = data().deepCopy(fields()[2].schema(), other.analysisState);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.analysisType)) {
        this.analysisType = data().deepCopy(fields()[3].schema(), other.analysisType);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
      if (isValidValue(fields()[4], other.studyId)) {
        this.studyId = data().deepCopy(fields()[4].schema(), other.studyId);
        fieldSetFlags()[4] = other.fieldSetFlags()[4];
      }
      if (isValidValue(fields()[5], other.experimentalStrategy)) {
        this.experimentalStrategy =
            data().deepCopy(fields()[5].schema(), other.experimentalStrategy);
        fieldSetFlags()[5] = other.fieldSetFlags()[5];
      }
      if (isValidValue(fields()[6], other.donorIds)) {
        this.donorIds = data().deepCopy(fields()[6].schema(), other.donorIds);
        fieldSetFlags()[6] = other.fieldSetFlags()[6];
      }
      if (isValidValue(fields()[7], other.files)) {
        this.files = data().deepCopy(fields()[7].schema(), other.files);
        fieldSetFlags()[7] = other.fieldSetFlags()[7];
      }
    }

    /**
     * Creates a Builder by copying an existing GraphEvent instance
     *
     * @param other The existing instance to copy.
     */
    private Builder(org.icgc_argo.workflow_graph_lib.schema.GraphEvent other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.analysisId)) {
        this.analysisId = data().deepCopy(fields()[1].schema(), other.analysisId);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.analysisState)) {
        this.analysisState = data().deepCopy(fields()[2].schema(), other.analysisState);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.analysisType)) {
        this.analysisType = data().deepCopy(fields()[3].schema(), other.analysisType);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.studyId)) {
        this.studyId = data().deepCopy(fields()[4].schema(), other.studyId);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.experimentalStrategy)) {
        this.experimentalStrategy =
            data().deepCopy(fields()[5].schema(), other.experimentalStrategy);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.donorIds)) {
        this.donorIds = data().deepCopy(fields()[6].schema(), other.donorIds);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.files)) {
        this.files = data().deepCopy(fields()[7].schema(), other.files);
        fieldSetFlags()[7] = true;
      }
    }

    /**
     * Gets the value of the 'id' field.
     *
     * @return The value.
     */
    public java.lang.String getId() {
      return id;
    }

    /**
     * Sets the value of the 'id' field.
     *
     * @param value The value of 'id'.
     * @return This builder.
     */
    public org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder setId(
        java.lang.String value) {
      validate(fields()[0], value);
      this.id = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
     * Checks whether the 'id' field has been set.
     *
     * @return True if the 'id' field has been set, false otherwise.
     */
    public boolean hasId() {
      return fieldSetFlags()[0];
    }

    /**
     * Clears the value of the 'id' field.
     *
     * @return This builder.
     */
    public org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder clearId() {
      id = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
     * Gets the value of the 'analysisId' field.
     *
     * @return The value.
     */
    public java.lang.String getAnalysisId() {
      return analysisId;
    }

    /**
     * Sets the value of the 'analysisId' field.
     *
     * @param value The value of 'analysisId'.
     * @return This builder.
     */
    public org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder setAnalysisId(
        java.lang.String value) {
      validate(fields()[1], value);
      this.analysisId = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
     * Checks whether the 'analysisId' field has been set.
     *
     * @return True if the 'analysisId' field has been set, false otherwise.
     */
    public boolean hasAnalysisId() {
      return fieldSetFlags()[1];
    }

    /**
     * Clears the value of the 'analysisId' field.
     *
     * @return This builder.
     */
    public org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder clearAnalysisId() {
      analysisId = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
     * Gets the value of the 'analysisState' field.
     *
     * @return The value.
     */
    public java.lang.String getAnalysisState() {
      return analysisState;
    }

    /**
     * Sets the value of the 'analysisState' field.
     *
     * @param value The value of 'analysisState'.
     * @return This builder.
     */
    public org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder setAnalysisState(
        java.lang.String value) {
      validate(fields()[2], value);
      this.analysisState = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
     * Checks whether the 'analysisState' field has been set.
     *
     * @return True if the 'analysisState' field has been set, false otherwise.
     */
    public boolean hasAnalysisState() {
      return fieldSetFlags()[2];
    }

    /**
     * Clears the value of the 'analysisState' field.
     *
     * @return This builder.
     */
    public org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder clearAnalysisState() {
      analysisState = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
     * Gets the value of the 'analysisType' field.
     *
     * @return The value.
     */
    public java.lang.String getAnalysisType() {
      return analysisType;
    }

    /**
     * Sets the value of the 'analysisType' field.
     *
     * @param value The value of 'analysisType'.
     * @return This builder.
     */
    public org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder setAnalysisType(
        java.lang.String value) {
      validate(fields()[3], value);
      this.analysisType = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
     * Checks whether the 'analysisType' field has been set.
     *
     * @return True if the 'analysisType' field has been set, false otherwise.
     */
    public boolean hasAnalysisType() {
      return fieldSetFlags()[3];
    }

    /**
     * Clears the value of the 'analysisType' field.
     *
     * @return This builder.
     */
    public org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder clearAnalysisType() {
      analysisType = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
     * Gets the value of the 'studyId' field.
     *
     * @return The value.
     */
    public java.lang.String getStudyId() {
      return studyId;
    }

    /**
     * Sets the value of the 'studyId' field.
     *
     * @param value The value of 'studyId'.
     * @return This builder.
     */
    public org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder setStudyId(
        java.lang.String value) {
      validate(fields()[4], value);
      this.studyId = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
     * Checks whether the 'studyId' field has been set.
     *
     * @return True if the 'studyId' field has been set, false otherwise.
     */
    public boolean hasStudyId() {
      return fieldSetFlags()[4];
    }

    /**
     * Clears the value of the 'studyId' field.
     *
     * @return This builder.
     */
    public org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder clearStudyId() {
      studyId = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    /**
     * Gets the value of the 'experimentalStrategy' field.
     *
     * @return The value.
     */
    public java.lang.String getExperimentalStrategy() {
      return experimentalStrategy;
    }

    /**
     * Sets the value of the 'experimentalStrategy' field.
     *
     * @param value The value of 'experimentalStrategy'.
     * @return This builder.
     */
    public org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder setExperimentalStrategy(
        java.lang.String value) {
      validate(fields()[5], value);
      this.experimentalStrategy = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
     * Checks whether the 'experimentalStrategy' field has been set.
     *
     * @return True if the 'experimentalStrategy' field has been set, false otherwise.
     */
    public boolean hasExperimentalStrategy() {
      return fieldSetFlags()[5];
    }

    /**
     * Clears the value of the 'experimentalStrategy' field.
     *
     * @return This builder.
     */
    public org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder clearExperimentalStrategy() {
      experimentalStrategy = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    /**
     * Gets the value of the 'donorIds' field.
     *
     * @return The value.
     */
    public java.util.List<java.lang.String> getDonorIds() {
      return donorIds;
    }

    /**
     * Sets the value of the 'donorIds' field.
     *
     * @param value The value of 'donorIds'.
     * @return This builder.
     */
    public org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder setDonorIds(
        java.util.List<java.lang.String> value) {
      validate(fields()[6], value);
      this.donorIds = value;
      fieldSetFlags()[6] = true;
      return this;
    }

    /**
     * Checks whether the 'donorIds' field has been set.
     *
     * @return True if the 'donorIds' field has been set, false otherwise.
     */
    public boolean hasDonorIds() {
      return fieldSetFlags()[6];
    }

    /**
     * Clears the value of the 'donorIds' field.
     *
     * @return This builder.
     */
    public org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder clearDonorIds() {
      donorIds = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    /**
     * Gets the value of the 'files' field.
     *
     * @return The value.
     */
    public java.util.List<org.icgc_argo.workflow_graph_lib.schema.AnalysisFile> getFiles() {
      return files;
    }

    /**
     * Sets the value of the 'files' field.
     *
     * @param value The value of 'files'.
     * @return This builder.
     */
    public org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder setFiles(
        java.util.List<org.icgc_argo.workflow_graph_lib.schema.AnalysisFile> value) {
      validate(fields()[7], value);
      this.files = value;
      fieldSetFlags()[7] = true;
      return this;
    }

    /**
     * Checks whether the 'files' field has been set.
     *
     * @return True if the 'files' field has been set, false otherwise.
     */
    public boolean hasFiles() {
      return fieldSetFlags()[7];
    }

    /**
     * Clears the value of the 'files' field.
     *
     * @return This builder.
     */
    public org.icgc_argo.workflow_graph_lib.schema.GraphEvent.Builder clearFiles() {
      files = null;
      fieldSetFlags()[7] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GraphEvent build() {
      try {
        GraphEvent record = new GraphEvent();
        record.id = fieldSetFlags()[0] ? this.id : (java.lang.String) defaultValue(fields()[0]);
        record.analysisId =
            fieldSetFlags()[1] ? this.analysisId : (java.lang.String) defaultValue(fields()[1]);
        record.analysisState =
            fieldSetFlags()[2] ? this.analysisState : (java.lang.String) defaultValue(fields()[2]);
        record.analysisType =
            fieldSetFlags()[3] ? this.analysisType : (java.lang.String) defaultValue(fields()[3]);
        record.studyId =
            fieldSetFlags()[4] ? this.studyId : (java.lang.String) defaultValue(fields()[4]);
        record.experimentalStrategy =
            fieldSetFlags()[5]
                ? this.experimentalStrategy
                : (java.lang.String) defaultValue(fields()[5]);
        record.donorIds =
            fieldSetFlags()[6]
                ? this.donorIds
                : (java.util.List<java.lang.String>) defaultValue(fields()[6]);
        record.files =
            fieldSetFlags()[7]
                ? this.files
                : (java.util.List<org.icgc_argo.workflow_graph_lib.schema.AnalysisFile>)
                    defaultValue(fields()[7]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<GraphEvent> WRITER$ =
      (org.apache.avro.io.DatumWriter<GraphEvent>) MODEL$.createDatumWriter(SCHEMA$);

  @Override
  public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<GraphEvent> READER$ =
      (org.apache.avro.io.DatumReader<GraphEvent>) MODEL$.createDatumReader(SCHEMA$);

  @Override
  public void readExternal(java.io.ObjectInput in) throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override
  protected boolean hasCustomCoders() {
    return true;
  }

  @Override
  public void customEncode(org.apache.avro.io.Encoder out) throws java.io.IOException {
    out.writeString(this.id);

    out.writeString(this.analysisId);

    out.writeString(this.analysisState);

    out.writeString(this.analysisType);

    out.writeString(this.studyId);

    if (this.experimentalStrategy == null) {
      out.writeIndex(1);
      out.writeNull();
    } else {
      out.writeIndex(0);
      out.writeString(this.experimentalStrategy);
    }

    long size0 = this.donorIds.size();
    out.writeArrayStart();
    out.setItemCount(size0);
    long actualSize0 = 0;
    for (java.lang.String e0 : this.donorIds) {
      actualSize0++;
      out.startItem();
      out.writeString(e0);
    }
    out.writeArrayEnd();
    if (actualSize0 != size0)
      throw new java.util.ConcurrentModificationException(
          "Array-size written was " + size0 + ", but element count was " + actualSize0 + ".");

    long size1 = this.files.size();
    out.writeArrayStart();
    out.setItemCount(size1);
    long actualSize1 = 0;
    for (org.icgc_argo.workflow_graph_lib.schema.AnalysisFile e1 : this.files) {
      actualSize1++;
      out.startItem();
      e1.customEncode(out);
    }
    out.writeArrayEnd();
    if (actualSize1 != size1)
      throw new java.util.ConcurrentModificationException(
          "Array-size written was " + size1 + ", but element count was " + actualSize1 + ".");
  }

  @Override
  public void customDecode(org.apache.avro.io.ResolvingDecoder in) throws java.io.IOException {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.id = in.readString();

      this.analysisId = in.readString();

      this.analysisState = in.readString();

      this.analysisType = in.readString();

      this.studyId = in.readString();

      if (in.readIndex() != 0) {
        in.readNull();
        this.experimentalStrategy = null;
      } else {
        this.experimentalStrategy = in.readString();
      }

      long size0 = in.readArrayStart();
      java.util.List<java.lang.String> a0 = this.donorIds;
      if (a0 == null) {
        a0 =
            new SpecificData.Array<java.lang.String>(
                (int) size0, SCHEMA$.getField("donorIds").schema());
        this.donorIds = a0;
      } else a0.clear();
      SpecificData.Array<java.lang.String> ga0 =
          (a0 instanceof SpecificData.Array ? (SpecificData.Array<java.lang.String>) a0 : null);
      for (; 0 < size0; size0 = in.arrayNext()) {
        for (; size0 != 0; size0--) {
          java.lang.String e0 = (ga0 != null ? ga0.peek() : null);
          e0 = in.readString();
          a0.add(e0);
        }
      }

      long size1 = in.readArrayStart();
      java.util.List<org.icgc_argo.workflow_graph_lib.schema.AnalysisFile> a1 = this.files;
      if (a1 == null) {
        a1 =
            new SpecificData.Array<org.icgc_argo.workflow_graph_lib.schema.AnalysisFile>(
                (int) size1, SCHEMA$.getField("files").schema());
        this.files = a1;
      } else a1.clear();
      SpecificData.Array<org.icgc_argo.workflow_graph_lib.schema.AnalysisFile> ga1 =
          (a1 instanceof SpecificData.Array
              ? (SpecificData.Array<org.icgc_argo.workflow_graph_lib.schema.AnalysisFile>) a1
              : null);
      for (; 0 < size1; size1 = in.arrayNext()) {
        for (; size1 != 0; size1--) {
          org.icgc_argo.workflow_graph_lib.schema.AnalysisFile e1 =
              (ga1 != null ? ga1.peek() : null);
          if (e1 == null) {
            e1 = new org.icgc_argo.workflow_graph_lib.schema.AnalysisFile();
          }
          e1.customDecode(in);
          a1.add(e1);
        }
      }

    } else {
      for (int i = 0; i < 8; i++) {
        switch (fieldOrder[i].pos()) {
          case 0:
            this.id = in.readString();
            break;

          case 1:
            this.analysisId = in.readString();
            break;

          case 2:
            this.analysisState = in.readString();
            break;

          case 3:
            this.analysisType = in.readString();
            break;

          case 4:
            this.studyId = in.readString();
            break;

          case 5:
            if (in.readIndex() != 0) {
              in.readNull();
              this.experimentalStrategy = null;
            } else {
              this.experimentalStrategy = in.readString();
            }
            break;

          case 6:
            long size0 = in.readArrayStart();
            java.util.List<java.lang.String> a0 = this.donorIds;
            if (a0 == null) {
              a0 =
                  new SpecificData.Array<java.lang.String>(
                      (int) size0, SCHEMA$.getField("donorIds").schema());
              this.donorIds = a0;
            } else a0.clear();
            SpecificData.Array<java.lang.String> ga0 =
                (a0 instanceof SpecificData.Array
                    ? (SpecificData.Array<java.lang.String>) a0
                    : null);
            for (; 0 < size0; size0 = in.arrayNext()) {
              for (; size0 != 0; size0--) {
                java.lang.String e0 = (ga0 != null ? ga0.peek() : null);
                e0 = in.readString();
                a0.add(e0);
              }
            }
            break;

          case 7:
            long size1 = in.readArrayStart();
            java.util.List<org.icgc_argo.workflow_graph_lib.schema.AnalysisFile> a1 = this.files;
            if (a1 == null) {
              a1 =
                  new SpecificData.Array<org.icgc_argo.workflow_graph_lib.schema.AnalysisFile>(
                      (int) size1, SCHEMA$.getField("files").schema());
              this.files = a1;
            } else a1.clear();
            SpecificData.Array<org.icgc_argo.workflow_graph_lib.schema.AnalysisFile> ga1 =
                (a1 instanceof SpecificData.Array
                    ? (SpecificData.Array<org.icgc_argo.workflow_graph_lib.schema.AnalysisFile>) a1
                    : null);
            for (; 0 < size1; size1 = in.arrayNext()) {
              for (; size1 != 0; size1--) {
                org.icgc_argo.workflow_graph_lib.schema.AnalysisFile e1 =
                    (ga1 != null ? ga1.peek() : null);
                if (e1 == null) {
                  e1 = new org.icgc_argo.workflow_graph_lib.schema.AnalysisFile();
                }
                e1.customDecode(in);
                a1.add(e1);
              }
            }
            break;

          default:
            throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}
