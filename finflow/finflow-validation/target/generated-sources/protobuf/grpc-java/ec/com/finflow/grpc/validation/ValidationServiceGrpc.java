package ec.com.finflow.grpc.validation;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * ============================================================
 * SERVICIO DE VALIDACIÓN
 * ============================================================
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: validation.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ValidationServiceGrpc {

  private ValidationServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "finflow.validation.ValidationService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.ValidateTransferRequest,
      ec.com.finflow.grpc.validation.ValidationResult> getValidateTransferMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ValidateTransfer",
      requestType = ec.com.finflow.grpc.validation.ValidateTransferRequest.class,
      responseType = ec.com.finflow.grpc.validation.ValidationResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.ValidateTransferRequest,
      ec.com.finflow.grpc.validation.ValidationResult> getValidateTransferMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.ValidateTransferRequest, ec.com.finflow.grpc.validation.ValidationResult> getValidateTransferMethod;
    if ((getValidateTransferMethod = ValidationServiceGrpc.getValidateTransferMethod) == null) {
      synchronized (ValidationServiceGrpc.class) {
        if ((getValidateTransferMethod = ValidationServiceGrpc.getValidateTransferMethod) == null) {
          ValidationServiceGrpc.getValidateTransferMethod = getValidateTransferMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.validation.ValidateTransferRequest, ec.com.finflow.grpc.validation.ValidationResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ValidateTransfer"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.validation.ValidateTransferRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.validation.ValidationResult.getDefaultInstance()))
              .setSchemaDescriptor(new ValidationServiceMethodDescriptorSupplier("ValidateTransfer"))
              .build();
        }
      }
    }
    return getValidateTransferMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.ValidateAccountsRequest,
      ec.com.finflow.grpc.validation.ValidateAccountsResponse> getValidateAccountsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ValidateAccounts",
      requestType = ec.com.finflow.grpc.validation.ValidateAccountsRequest.class,
      responseType = ec.com.finflow.grpc.validation.ValidateAccountsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.ValidateAccountsRequest,
      ec.com.finflow.grpc.validation.ValidateAccountsResponse> getValidateAccountsMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.ValidateAccountsRequest, ec.com.finflow.grpc.validation.ValidateAccountsResponse> getValidateAccountsMethod;
    if ((getValidateAccountsMethod = ValidationServiceGrpc.getValidateAccountsMethod) == null) {
      synchronized (ValidationServiceGrpc.class) {
        if ((getValidateAccountsMethod = ValidationServiceGrpc.getValidateAccountsMethod) == null) {
          ValidationServiceGrpc.getValidateAccountsMethod = getValidateAccountsMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.validation.ValidateAccountsRequest, ec.com.finflow.grpc.validation.ValidateAccountsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ValidateAccounts"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.validation.ValidateAccountsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.validation.ValidateAccountsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ValidationServiceMethodDescriptorSupplier("ValidateAccounts"))
              .build();
        }
      }
    }
    return getValidateAccountsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.FraudScoreRequest,
      ec.com.finflow.grpc.validation.FraudScoreResponse> getCalculateFraudScoreMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CalculateFraudScore",
      requestType = ec.com.finflow.grpc.validation.FraudScoreRequest.class,
      responseType = ec.com.finflow.grpc.validation.FraudScoreResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.FraudScoreRequest,
      ec.com.finflow.grpc.validation.FraudScoreResponse> getCalculateFraudScoreMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.FraudScoreRequest, ec.com.finflow.grpc.validation.FraudScoreResponse> getCalculateFraudScoreMethod;
    if ((getCalculateFraudScoreMethod = ValidationServiceGrpc.getCalculateFraudScoreMethod) == null) {
      synchronized (ValidationServiceGrpc.class) {
        if ((getCalculateFraudScoreMethod = ValidationServiceGrpc.getCalculateFraudScoreMethod) == null) {
          ValidationServiceGrpc.getCalculateFraudScoreMethod = getCalculateFraudScoreMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.validation.FraudScoreRequest, ec.com.finflow.grpc.validation.FraudScoreResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CalculateFraudScore"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.validation.FraudScoreRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.validation.FraudScoreResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ValidationServiceMethodDescriptorSupplier("CalculateFraudScore"))
              .build();
        }
      }
    }
    return getCalculateFraudScoreMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.GetUserProfileRequest,
      ec.com.finflow.grpc.validation.UserTransferProfile> getGetUserTransferProfileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetUserTransferProfile",
      requestType = ec.com.finflow.grpc.validation.GetUserProfileRequest.class,
      responseType = ec.com.finflow.grpc.validation.UserTransferProfile.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.GetUserProfileRequest,
      ec.com.finflow.grpc.validation.UserTransferProfile> getGetUserTransferProfileMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.GetUserProfileRequest, ec.com.finflow.grpc.validation.UserTransferProfile> getGetUserTransferProfileMethod;
    if ((getGetUserTransferProfileMethod = ValidationServiceGrpc.getGetUserTransferProfileMethod) == null) {
      synchronized (ValidationServiceGrpc.class) {
        if ((getGetUserTransferProfileMethod = ValidationServiceGrpc.getGetUserTransferProfileMethod) == null) {
          ValidationServiceGrpc.getGetUserTransferProfileMethod = getGetUserTransferProfileMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.validation.GetUserProfileRequest, ec.com.finflow.grpc.validation.UserTransferProfile>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetUserTransferProfile"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.validation.GetUserProfileRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.validation.UserTransferProfile.getDefaultInstance()))
              .setSchemaDescriptor(new ValidationServiceMethodDescriptorSupplier("GetUserTransferProfile"))
              .build();
        }
      }
    }
    return getGetUserTransferProfileMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.BlacklistRequest,
      ec.com.finflow.grpc.validation.BlacklistResponse> getAddToBlacklistMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AddToBlacklist",
      requestType = ec.com.finflow.grpc.validation.BlacklistRequest.class,
      responseType = ec.com.finflow.grpc.validation.BlacklistResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.BlacklistRequest,
      ec.com.finflow.grpc.validation.BlacklistResponse> getAddToBlacklistMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.BlacklistRequest, ec.com.finflow.grpc.validation.BlacklistResponse> getAddToBlacklistMethod;
    if ((getAddToBlacklistMethod = ValidationServiceGrpc.getAddToBlacklistMethod) == null) {
      synchronized (ValidationServiceGrpc.class) {
        if ((getAddToBlacklistMethod = ValidationServiceGrpc.getAddToBlacklistMethod) == null) {
          ValidationServiceGrpc.getAddToBlacklistMethod = getAddToBlacklistMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.validation.BlacklistRequest, ec.com.finflow.grpc.validation.BlacklistResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AddToBlacklist"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.validation.BlacklistRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.validation.BlacklistResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ValidationServiceMethodDescriptorSupplier("AddToBlacklist"))
              .build();
        }
      }
    }
    return getAddToBlacklistMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.RemoveBlacklistRequest,
      ec.com.finflow.grpc.validation.BlacklistResponse> getRemoveFromBlacklistMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RemoveFromBlacklist",
      requestType = ec.com.finflow.grpc.validation.RemoveBlacklistRequest.class,
      responseType = ec.com.finflow.grpc.validation.BlacklistResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.RemoveBlacklistRequest,
      ec.com.finflow.grpc.validation.BlacklistResponse> getRemoveFromBlacklistMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.RemoveBlacklistRequest, ec.com.finflow.grpc.validation.BlacklistResponse> getRemoveFromBlacklistMethod;
    if ((getRemoveFromBlacklistMethod = ValidationServiceGrpc.getRemoveFromBlacklistMethod) == null) {
      synchronized (ValidationServiceGrpc.class) {
        if ((getRemoveFromBlacklistMethod = ValidationServiceGrpc.getRemoveFromBlacklistMethod) == null) {
          ValidationServiceGrpc.getRemoveFromBlacklistMethod = getRemoveFromBlacklistMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.validation.RemoveBlacklistRequest, ec.com.finflow.grpc.validation.BlacklistResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RemoveFromBlacklist"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.validation.RemoveBlacklistRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.validation.BlacklistResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ValidationServiceMethodDescriptorSupplier("RemoveFromBlacklist"))
              .build();
        }
      }
    }
    return getRemoveFromBlacklistMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.CheckBlacklistRequest,
      ec.com.finflow.grpc.validation.CheckBlacklistResponse> getCheckBlacklistMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CheckBlacklist",
      requestType = ec.com.finflow.grpc.validation.CheckBlacklistRequest.class,
      responseType = ec.com.finflow.grpc.validation.CheckBlacklistResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.CheckBlacklistRequest,
      ec.com.finflow.grpc.validation.CheckBlacklistResponse> getCheckBlacklistMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.CheckBlacklistRequest, ec.com.finflow.grpc.validation.CheckBlacklistResponse> getCheckBlacklistMethod;
    if ((getCheckBlacklistMethod = ValidationServiceGrpc.getCheckBlacklistMethod) == null) {
      synchronized (ValidationServiceGrpc.class) {
        if ((getCheckBlacklistMethod = ValidationServiceGrpc.getCheckBlacklistMethod) == null) {
          ValidationServiceGrpc.getCheckBlacklistMethod = getCheckBlacklistMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.validation.CheckBlacklistRequest, ec.com.finflow.grpc.validation.CheckBlacklistResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CheckBlacklist"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.validation.CheckBlacklistRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.validation.CheckBlacklistResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ValidationServiceMethodDescriptorSupplier("CheckBlacklist"))
              .build();
        }
      }
    }
    return getCheckBlacklistMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.ValidationHistoryRequest,
      ec.com.finflow.grpc.validation.ValidationHistoryResponse> getGetValidationHistoryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetValidationHistory",
      requestType = ec.com.finflow.grpc.validation.ValidationHistoryRequest.class,
      responseType = ec.com.finflow.grpc.validation.ValidationHistoryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.ValidationHistoryRequest,
      ec.com.finflow.grpc.validation.ValidationHistoryResponse> getGetValidationHistoryMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.ValidationHistoryRequest, ec.com.finflow.grpc.validation.ValidationHistoryResponse> getGetValidationHistoryMethod;
    if ((getGetValidationHistoryMethod = ValidationServiceGrpc.getGetValidationHistoryMethod) == null) {
      synchronized (ValidationServiceGrpc.class) {
        if ((getGetValidationHistoryMethod = ValidationServiceGrpc.getGetValidationHistoryMethod) == null) {
          ValidationServiceGrpc.getGetValidationHistoryMethod = getGetValidationHistoryMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.validation.ValidationHistoryRequest, ec.com.finflow.grpc.validation.ValidationHistoryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetValidationHistory"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.validation.ValidationHistoryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.validation.ValidationHistoryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ValidationServiceMethodDescriptorSupplier("GetValidationHistory"))
              .build();
        }
      }
    }
    return getGetValidationHistoryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.GetValidationDetailsRequest,
      ec.com.finflow.grpc.validation.ValidationDetails> getGetValidationDetailsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetValidationDetails",
      requestType = ec.com.finflow.grpc.validation.GetValidationDetailsRequest.class,
      responseType = ec.com.finflow.grpc.validation.ValidationDetails.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.GetValidationDetailsRequest,
      ec.com.finflow.grpc.validation.ValidationDetails> getGetValidationDetailsMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.validation.GetValidationDetailsRequest, ec.com.finflow.grpc.validation.ValidationDetails> getGetValidationDetailsMethod;
    if ((getGetValidationDetailsMethod = ValidationServiceGrpc.getGetValidationDetailsMethod) == null) {
      synchronized (ValidationServiceGrpc.class) {
        if ((getGetValidationDetailsMethod = ValidationServiceGrpc.getGetValidationDetailsMethod) == null) {
          ValidationServiceGrpc.getGetValidationDetailsMethod = getGetValidationDetailsMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.validation.GetValidationDetailsRequest, ec.com.finflow.grpc.validation.ValidationDetails>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetValidationDetails"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.validation.GetValidationDetailsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.validation.ValidationDetails.getDefaultInstance()))
              .setSchemaDescriptor(new ValidationServiceMethodDescriptorSupplier("GetValidationDetails"))
              .build();
        }
      }
    }
    return getGetValidationDetailsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ValidationServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ValidationServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ValidationServiceStub>() {
        @java.lang.Override
        public ValidationServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ValidationServiceStub(channel, callOptions);
        }
      };
    return ValidationServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ValidationServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ValidationServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ValidationServiceBlockingStub>() {
        @java.lang.Override
        public ValidationServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ValidationServiceBlockingStub(channel, callOptions);
        }
      };
    return ValidationServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ValidationServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ValidationServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ValidationServiceFutureStub>() {
        @java.lang.Override
        public ValidationServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ValidationServiceFutureStub(channel, callOptions);
        }
      };
    return ValidationServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * ============================================================
   * SERVICIO DE VALIDACIÓN
   * ============================================================
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Valida una transferencia completa (cuenta, saldo, fraude)
     * Este es el método principal llamado por finflow-transfers
     * </pre>
     */
    default void validateTransfer(ec.com.finflow.grpc.validation.ValidateTransferRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.ValidationResult> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getValidateTransferMethod(), responseObserver);
    }

    /**
     * <pre>
     * Validación rápida solo de cuentas (sin análisis de fraude)
     * </pre>
     */
    default void validateAccounts(ec.com.finflow.grpc.validation.ValidateAccountsRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.ValidateAccountsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getValidateAccountsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Calcula el fraud score sin rechazar
     * Útil para análisis y reportes
     * </pre>
     */
    default void calculateFraudScore(ec.com.finflow.grpc.validation.FraudScoreRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.FraudScoreResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCalculateFraudScoreMethod(), responseObserver);
    }

    /**
     * <pre>
     * Obtiene el perfil de transferencias de un usuario
     * </pre>
     */
    default void getUserTransferProfile(ec.com.finflow.grpc.validation.GetUserProfileRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.UserTransferProfile> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetUserTransferProfileMethod(), responseObserver);
    }

    /**
     * <pre>
     * Agrega una cuenta a la lista negra
     * </pre>
     */
    default void addToBlacklist(ec.com.finflow.grpc.validation.BlacklistRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.BlacklistResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAddToBlacklistMethod(), responseObserver);
    }

    /**
     * <pre>
     * Remueve una cuenta de la lista negra
     * </pre>
     */
    default void removeFromBlacklist(ec.com.finflow.grpc.validation.RemoveBlacklistRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.BlacklistResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRemoveFromBlacklistMethod(), responseObserver);
    }

    /**
     * <pre>
     * Verifica si una cuenta está en lista negra
     * </pre>
     */
    default void checkBlacklist(ec.com.finflow.grpc.validation.CheckBlacklistRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.CheckBlacklistResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCheckBlacklistMethod(), responseObserver);
    }

    /**
     * <pre>
     * Obtiene el historial de validaciones
     * </pre>
     */
    default void getValidationHistory(ec.com.finflow.grpc.validation.ValidationHistoryRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.ValidationHistoryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetValidationHistoryMethod(), responseObserver);
    }

    /**
     * <pre>
     * Obtiene detalles de una validación específica
     * </pre>
     */
    default void getValidationDetails(ec.com.finflow.grpc.validation.GetValidationDetailsRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.ValidationDetails> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetValidationDetailsMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service ValidationService.
   * <pre>
   * ============================================================
   * SERVICIO DE VALIDACIÓN
   * ============================================================
   * </pre>
   */
  public static abstract class ValidationServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return ValidationServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service ValidationService.
   * <pre>
   * ============================================================
   * SERVICIO DE VALIDACIÓN
   * ============================================================
   * </pre>
   */
  public static final class ValidationServiceStub
      extends io.grpc.stub.AbstractAsyncStub<ValidationServiceStub> {
    private ValidationServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ValidationServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ValidationServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Valida una transferencia completa (cuenta, saldo, fraude)
     * Este es el método principal llamado por finflow-transfers
     * </pre>
     */
    public void validateTransfer(ec.com.finflow.grpc.validation.ValidateTransferRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.ValidationResult> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getValidateTransferMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Validación rápida solo de cuentas (sin análisis de fraude)
     * </pre>
     */
    public void validateAccounts(ec.com.finflow.grpc.validation.ValidateAccountsRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.ValidateAccountsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getValidateAccountsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Calcula el fraud score sin rechazar
     * Útil para análisis y reportes
     * </pre>
     */
    public void calculateFraudScore(ec.com.finflow.grpc.validation.FraudScoreRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.FraudScoreResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCalculateFraudScoreMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Obtiene el perfil de transferencias de un usuario
     * </pre>
     */
    public void getUserTransferProfile(ec.com.finflow.grpc.validation.GetUserProfileRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.UserTransferProfile> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetUserTransferProfileMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Agrega una cuenta a la lista negra
     * </pre>
     */
    public void addToBlacklist(ec.com.finflow.grpc.validation.BlacklistRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.BlacklistResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAddToBlacklistMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Remueve una cuenta de la lista negra
     * </pre>
     */
    public void removeFromBlacklist(ec.com.finflow.grpc.validation.RemoveBlacklistRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.BlacklistResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRemoveFromBlacklistMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Verifica si una cuenta está en lista negra
     * </pre>
     */
    public void checkBlacklist(ec.com.finflow.grpc.validation.CheckBlacklistRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.CheckBlacklistResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCheckBlacklistMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Obtiene el historial de validaciones
     * </pre>
     */
    public void getValidationHistory(ec.com.finflow.grpc.validation.ValidationHistoryRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.ValidationHistoryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetValidationHistoryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Obtiene detalles de una validación específica
     * </pre>
     */
    public void getValidationDetails(ec.com.finflow.grpc.validation.GetValidationDetailsRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.ValidationDetails> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetValidationDetailsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service ValidationService.
   * <pre>
   * ============================================================
   * SERVICIO DE VALIDACIÓN
   * ============================================================
   * </pre>
   */
  public static final class ValidationServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<ValidationServiceBlockingStub> {
    private ValidationServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ValidationServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ValidationServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Valida una transferencia completa (cuenta, saldo, fraude)
     * Este es el método principal llamado por finflow-transfers
     * </pre>
     */
    public ec.com.finflow.grpc.validation.ValidationResult validateTransfer(ec.com.finflow.grpc.validation.ValidateTransferRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getValidateTransferMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Validación rápida solo de cuentas (sin análisis de fraude)
     * </pre>
     */
    public ec.com.finflow.grpc.validation.ValidateAccountsResponse validateAccounts(ec.com.finflow.grpc.validation.ValidateAccountsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getValidateAccountsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Calcula el fraud score sin rechazar
     * Útil para análisis y reportes
     * </pre>
     */
    public ec.com.finflow.grpc.validation.FraudScoreResponse calculateFraudScore(ec.com.finflow.grpc.validation.FraudScoreRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCalculateFraudScoreMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Obtiene el perfil de transferencias de un usuario
     * </pre>
     */
    public ec.com.finflow.grpc.validation.UserTransferProfile getUserTransferProfile(ec.com.finflow.grpc.validation.GetUserProfileRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserTransferProfileMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Agrega una cuenta a la lista negra
     * </pre>
     */
    public ec.com.finflow.grpc.validation.BlacklistResponse addToBlacklist(ec.com.finflow.grpc.validation.BlacklistRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddToBlacklistMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Remueve una cuenta de la lista negra
     * </pre>
     */
    public ec.com.finflow.grpc.validation.BlacklistResponse removeFromBlacklist(ec.com.finflow.grpc.validation.RemoveBlacklistRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRemoveFromBlacklistMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Verifica si una cuenta está en lista negra
     * </pre>
     */
    public ec.com.finflow.grpc.validation.CheckBlacklistResponse checkBlacklist(ec.com.finflow.grpc.validation.CheckBlacklistRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCheckBlacklistMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Obtiene el historial de validaciones
     * </pre>
     */
    public ec.com.finflow.grpc.validation.ValidationHistoryResponse getValidationHistory(ec.com.finflow.grpc.validation.ValidationHistoryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetValidationHistoryMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Obtiene detalles de una validación específica
     * </pre>
     */
    public ec.com.finflow.grpc.validation.ValidationDetails getValidationDetails(ec.com.finflow.grpc.validation.GetValidationDetailsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetValidationDetailsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service ValidationService.
   * <pre>
   * ============================================================
   * SERVICIO DE VALIDACIÓN
   * ============================================================
   * </pre>
   */
  public static final class ValidationServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<ValidationServiceFutureStub> {
    private ValidationServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ValidationServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ValidationServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Valida una transferencia completa (cuenta, saldo, fraude)
     * Este es el método principal llamado por finflow-transfers
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.validation.ValidationResult> validateTransfer(
        ec.com.finflow.grpc.validation.ValidateTransferRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getValidateTransferMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Validación rápida solo de cuentas (sin análisis de fraude)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.validation.ValidateAccountsResponse> validateAccounts(
        ec.com.finflow.grpc.validation.ValidateAccountsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getValidateAccountsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Calcula el fraud score sin rechazar
     * Útil para análisis y reportes
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.validation.FraudScoreResponse> calculateFraudScore(
        ec.com.finflow.grpc.validation.FraudScoreRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCalculateFraudScoreMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Obtiene el perfil de transferencias de un usuario
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.validation.UserTransferProfile> getUserTransferProfile(
        ec.com.finflow.grpc.validation.GetUserProfileRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetUserTransferProfileMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Agrega una cuenta a la lista negra
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.validation.BlacklistResponse> addToBlacklist(
        ec.com.finflow.grpc.validation.BlacklistRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAddToBlacklistMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Remueve una cuenta de la lista negra
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.validation.BlacklistResponse> removeFromBlacklist(
        ec.com.finflow.grpc.validation.RemoveBlacklistRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRemoveFromBlacklistMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Verifica si una cuenta está en lista negra
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.validation.CheckBlacklistResponse> checkBlacklist(
        ec.com.finflow.grpc.validation.CheckBlacklistRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCheckBlacklistMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Obtiene el historial de validaciones
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.validation.ValidationHistoryResponse> getValidationHistory(
        ec.com.finflow.grpc.validation.ValidationHistoryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetValidationHistoryMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Obtiene detalles de una validación específica
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.validation.ValidationDetails> getValidationDetails(
        ec.com.finflow.grpc.validation.GetValidationDetailsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetValidationDetailsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_VALIDATE_TRANSFER = 0;
  private static final int METHODID_VALIDATE_ACCOUNTS = 1;
  private static final int METHODID_CALCULATE_FRAUD_SCORE = 2;
  private static final int METHODID_GET_USER_TRANSFER_PROFILE = 3;
  private static final int METHODID_ADD_TO_BLACKLIST = 4;
  private static final int METHODID_REMOVE_FROM_BLACKLIST = 5;
  private static final int METHODID_CHECK_BLACKLIST = 6;
  private static final int METHODID_GET_VALIDATION_HISTORY = 7;
  private static final int METHODID_GET_VALIDATION_DETAILS = 8;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_VALIDATE_TRANSFER:
          serviceImpl.validateTransfer((ec.com.finflow.grpc.validation.ValidateTransferRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.ValidationResult>) responseObserver);
          break;
        case METHODID_VALIDATE_ACCOUNTS:
          serviceImpl.validateAccounts((ec.com.finflow.grpc.validation.ValidateAccountsRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.ValidateAccountsResponse>) responseObserver);
          break;
        case METHODID_CALCULATE_FRAUD_SCORE:
          serviceImpl.calculateFraudScore((ec.com.finflow.grpc.validation.FraudScoreRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.FraudScoreResponse>) responseObserver);
          break;
        case METHODID_GET_USER_TRANSFER_PROFILE:
          serviceImpl.getUserTransferProfile((ec.com.finflow.grpc.validation.GetUserProfileRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.UserTransferProfile>) responseObserver);
          break;
        case METHODID_ADD_TO_BLACKLIST:
          serviceImpl.addToBlacklist((ec.com.finflow.grpc.validation.BlacklistRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.BlacklistResponse>) responseObserver);
          break;
        case METHODID_REMOVE_FROM_BLACKLIST:
          serviceImpl.removeFromBlacklist((ec.com.finflow.grpc.validation.RemoveBlacklistRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.BlacklistResponse>) responseObserver);
          break;
        case METHODID_CHECK_BLACKLIST:
          serviceImpl.checkBlacklist((ec.com.finflow.grpc.validation.CheckBlacklistRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.CheckBlacklistResponse>) responseObserver);
          break;
        case METHODID_GET_VALIDATION_HISTORY:
          serviceImpl.getValidationHistory((ec.com.finflow.grpc.validation.ValidationHistoryRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.ValidationHistoryResponse>) responseObserver);
          break;
        case METHODID_GET_VALIDATION_DETAILS:
          serviceImpl.getValidationDetails((ec.com.finflow.grpc.validation.GetValidationDetailsRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.validation.ValidationDetails>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getValidateTransferMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.validation.ValidateTransferRequest,
              ec.com.finflow.grpc.validation.ValidationResult>(
                service, METHODID_VALIDATE_TRANSFER)))
        .addMethod(
          getValidateAccountsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.validation.ValidateAccountsRequest,
              ec.com.finflow.grpc.validation.ValidateAccountsResponse>(
                service, METHODID_VALIDATE_ACCOUNTS)))
        .addMethod(
          getCalculateFraudScoreMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.validation.FraudScoreRequest,
              ec.com.finflow.grpc.validation.FraudScoreResponse>(
                service, METHODID_CALCULATE_FRAUD_SCORE)))
        .addMethod(
          getGetUserTransferProfileMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.validation.GetUserProfileRequest,
              ec.com.finflow.grpc.validation.UserTransferProfile>(
                service, METHODID_GET_USER_TRANSFER_PROFILE)))
        .addMethod(
          getAddToBlacklistMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.validation.BlacklistRequest,
              ec.com.finflow.grpc.validation.BlacklistResponse>(
                service, METHODID_ADD_TO_BLACKLIST)))
        .addMethod(
          getRemoveFromBlacklistMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.validation.RemoveBlacklistRequest,
              ec.com.finflow.grpc.validation.BlacklistResponse>(
                service, METHODID_REMOVE_FROM_BLACKLIST)))
        .addMethod(
          getCheckBlacklistMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.validation.CheckBlacklistRequest,
              ec.com.finflow.grpc.validation.CheckBlacklistResponse>(
                service, METHODID_CHECK_BLACKLIST)))
        .addMethod(
          getGetValidationHistoryMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.validation.ValidationHistoryRequest,
              ec.com.finflow.grpc.validation.ValidationHistoryResponse>(
                service, METHODID_GET_VALIDATION_HISTORY)))
        .addMethod(
          getGetValidationDetailsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.validation.GetValidationDetailsRequest,
              ec.com.finflow.grpc.validation.ValidationDetails>(
                service, METHODID_GET_VALIDATION_DETAILS)))
        .build();
  }

  private static abstract class ValidationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ValidationServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return ec.com.finflow.grpc.validation.ValidationProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ValidationService");
    }
  }

  private static final class ValidationServiceFileDescriptorSupplier
      extends ValidationServiceBaseDescriptorSupplier {
    ValidationServiceFileDescriptorSupplier() {}
  }

  private static final class ValidationServiceMethodDescriptorSupplier
      extends ValidationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    ValidationServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ValidationServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ValidationServiceFileDescriptorSupplier())
              .addMethod(getValidateTransferMethod())
              .addMethod(getValidateAccountsMethod())
              .addMethod(getCalculateFraudScoreMethod())
              .addMethod(getGetUserTransferProfileMethod())
              .addMethod(getAddToBlacklistMethod())
              .addMethod(getRemoveFromBlacklistMethod())
              .addMethod(getCheckBlacklistMethod())
              .addMethod(getGetValidationHistoryMethod())
              .addMethod(getGetValidationDetailsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
