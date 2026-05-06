package ec.com.finflow.grpc.accounts;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * ============================================================
 * SERVICIO PRINCIPAL DE CUENTAS
 * ============================================================
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: accounts.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class AccountServiceGrpc {

  private AccountServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "finflow.accounts.AccountService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.GetAccountRequest,
      ec.com.finflow.grpc.accounts.AccountResponse> getGetAccountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetAccount",
      requestType = ec.com.finflow.grpc.accounts.GetAccountRequest.class,
      responseType = ec.com.finflow.grpc.accounts.AccountResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.GetAccountRequest,
      ec.com.finflow.grpc.accounts.AccountResponse> getGetAccountMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.GetAccountRequest, ec.com.finflow.grpc.accounts.AccountResponse> getGetAccountMethod;
    if ((getGetAccountMethod = AccountServiceGrpc.getGetAccountMethod) == null) {
      synchronized (AccountServiceGrpc.class) {
        if ((getGetAccountMethod = AccountServiceGrpc.getGetAccountMethod) == null) {
          AccountServiceGrpc.getGetAccountMethod = getGetAccountMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.accounts.GetAccountRequest, ec.com.finflow.grpc.accounts.AccountResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetAccount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.GetAccountRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.AccountResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountServiceMethodDescriptorSupplier("GetAccount"))
              .build();
        }
      }
    }
    return getGetAccountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.GetBalanceRequest,
      ec.com.finflow.grpc.accounts.BalanceResponse> getGetBalanceMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetBalance",
      requestType = ec.com.finflow.grpc.accounts.GetBalanceRequest.class,
      responseType = ec.com.finflow.grpc.accounts.BalanceResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.GetBalanceRequest,
      ec.com.finflow.grpc.accounts.BalanceResponse> getGetBalanceMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.GetBalanceRequest, ec.com.finflow.grpc.accounts.BalanceResponse> getGetBalanceMethod;
    if ((getGetBalanceMethod = AccountServiceGrpc.getGetBalanceMethod) == null) {
      synchronized (AccountServiceGrpc.class) {
        if ((getGetBalanceMethod = AccountServiceGrpc.getGetBalanceMethod) == null) {
          AccountServiceGrpc.getGetBalanceMethod = getGetBalanceMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.accounts.GetBalanceRequest, ec.com.finflow.grpc.accounts.BalanceResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetBalance"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.GetBalanceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.BalanceResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountServiceMethodDescriptorSupplier("GetBalance"))
              .build();
        }
      }
    }
    return getGetBalanceMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.ValidateAccountRequest,
      ec.com.finflow.grpc.accounts.ValidateAccountResponse> getValidateAccountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ValidateAccount",
      requestType = ec.com.finflow.grpc.accounts.ValidateAccountRequest.class,
      responseType = ec.com.finflow.grpc.accounts.ValidateAccountResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.ValidateAccountRequest,
      ec.com.finflow.grpc.accounts.ValidateAccountResponse> getValidateAccountMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.ValidateAccountRequest, ec.com.finflow.grpc.accounts.ValidateAccountResponse> getValidateAccountMethod;
    if ((getValidateAccountMethod = AccountServiceGrpc.getValidateAccountMethod) == null) {
      synchronized (AccountServiceGrpc.class) {
        if ((getValidateAccountMethod = AccountServiceGrpc.getValidateAccountMethod) == null) {
          AccountServiceGrpc.getValidateAccountMethod = getValidateAccountMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.accounts.ValidateAccountRequest, ec.com.finflow.grpc.accounts.ValidateAccountResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ValidateAccount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.ValidateAccountRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.ValidateAccountResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountServiceMethodDescriptorSupplier("ValidateAccount"))
              .build();
        }
      }
    }
    return getValidateAccountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.BlockAmountRequest,
      ec.com.finflow.grpc.accounts.BlockResponse> getBlockAmountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "BlockAmount",
      requestType = ec.com.finflow.grpc.accounts.BlockAmountRequest.class,
      responseType = ec.com.finflow.grpc.accounts.BlockResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.BlockAmountRequest,
      ec.com.finflow.grpc.accounts.BlockResponse> getBlockAmountMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.BlockAmountRequest, ec.com.finflow.grpc.accounts.BlockResponse> getBlockAmountMethod;
    if ((getBlockAmountMethod = AccountServiceGrpc.getBlockAmountMethod) == null) {
      synchronized (AccountServiceGrpc.class) {
        if ((getBlockAmountMethod = AccountServiceGrpc.getBlockAmountMethod) == null) {
          AccountServiceGrpc.getBlockAmountMethod = getBlockAmountMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.accounts.BlockAmountRequest, ec.com.finflow.grpc.accounts.BlockResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "BlockAmount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.BlockAmountRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.BlockResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountServiceMethodDescriptorSupplier("BlockAmount"))
              .build();
        }
      }
    }
    return getBlockAmountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.ReleaseBlockRequest,
      ec.com.finflow.grpc.accounts.ReleaseResponse> getReleaseBlockMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReleaseBlock",
      requestType = ec.com.finflow.grpc.accounts.ReleaseBlockRequest.class,
      responseType = ec.com.finflow.grpc.accounts.ReleaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.ReleaseBlockRequest,
      ec.com.finflow.grpc.accounts.ReleaseResponse> getReleaseBlockMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.ReleaseBlockRequest, ec.com.finflow.grpc.accounts.ReleaseResponse> getReleaseBlockMethod;
    if ((getReleaseBlockMethod = AccountServiceGrpc.getReleaseBlockMethod) == null) {
      synchronized (AccountServiceGrpc.class) {
        if ((getReleaseBlockMethod = AccountServiceGrpc.getReleaseBlockMethod) == null) {
          AccountServiceGrpc.getReleaseBlockMethod = getReleaseBlockMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.accounts.ReleaseBlockRequest, ec.com.finflow.grpc.accounts.ReleaseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReleaseBlock"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.ReleaseBlockRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.ReleaseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountServiceMethodDescriptorSupplier("ReleaseBlock"))
              .build();
        }
      }
    }
    return getReleaseBlockMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.ExecuteBlockedDebitRequest,
      ec.com.finflow.grpc.accounts.TransactionResponse> getExecuteBlockedDebitMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ExecuteBlockedDebit",
      requestType = ec.com.finflow.grpc.accounts.ExecuteBlockedDebitRequest.class,
      responseType = ec.com.finflow.grpc.accounts.TransactionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.ExecuteBlockedDebitRequest,
      ec.com.finflow.grpc.accounts.TransactionResponse> getExecuteBlockedDebitMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.ExecuteBlockedDebitRequest, ec.com.finflow.grpc.accounts.TransactionResponse> getExecuteBlockedDebitMethod;
    if ((getExecuteBlockedDebitMethod = AccountServiceGrpc.getExecuteBlockedDebitMethod) == null) {
      synchronized (AccountServiceGrpc.class) {
        if ((getExecuteBlockedDebitMethod = AccountServiceGrpc.getExecuteBlockedDebitMethod) == null) {
          AccountServiceGrpc.getExecuteBlockedDebitMethod = getExecuteBlockedDebitMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.accounts.ExecuteBlockedDebitRequest, ec.com.finflow.grpc.accounts.TransactionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ExecuteBlockedDebit"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.ExecuteBlockedDebitRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.TransactionResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountServiceMethodDescriptorSupplier("ExecuteBlockedDebit"))
              .build();
        }
      }
    }
    return getExecuteBlockedDebitMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.ExecuteDebitRequest,
      ec.com.finflow.grpc.accounts.TransactionResponse> getExecuteDebitMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ExecuteDebit",
      requestType = ec.com.finflow.grpc.accounts.ExecuteDebitRequest.class,
      responseType = ec.com.finflow.grpc.accounts.TransactionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.ExecuteDebitRequest,
      ec.com.finflow.grpc.accounts.TransactionResponse> getExecuteDebitMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.ExecuteDebitRequest, ec.com.finflow.grpc.accounts.TransactionResponse> getExecuteDebitMethod;
    if ((getExecuteDebitMethod = AccountServiceGrpc.getExecuteDebitMethod) == null) {
      synchronized (AccountServiceGrpc.class) {
        if ((getExecuteDebitMethod = AccountServiceGrpc.getExecuteDebitMethod) == null) {
          AccountServiceGrpc.getExecuteDebitMethod = getExecuteDebitMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.accounts.ExecuteDebitRequest, ec.com.finflow.grpc.accounts.TransactionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ExecuteDebit"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.ExecuteDebitRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.TransactionResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountServiceMethodDescriptorSupplier("ExecuteDebit"))
              .build();
        }
      }
    }
    return getExecuteDebitMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.ExecuteCreditRequest,
      ec.com.finflow.grpc.accounts.TransactionResponse> getExecuteCreditMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ExecuteCredit",
      requestType = ec.com.finflow.grpc.accounts.ExecuteCreditRequest.class,
      responseType = ec.com.finflow.grpc.accounts.TransactionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.ExecuteCreditRequest,
      ec.com.finflow.grpc.accounts.TransactionResponse> getExecuteCreditMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.ExecuteCreditRequest, ec.com.finflow.grpc.accounts.TransactionResponse> getExecuteCreditMethod;
    if ((getExecuteCreditMethod = AccountServiceGrpc.getExecuteCreditMethod) == null) {
      synchronized (AccountServiceGrpc.class) {
        if ((getExecuteCreditMethod = AccountServiceGrpc.getExecuteCreditMethod) == null) {
          AccountServiceGrpc.getExecuteCreditMethod = getExecuteCreditMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.accounts.ExecuteCreditRequest, ec.com.finflow.grpc.accounts.TransactionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ExecuteCredit"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.ExecuteCreditRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.TransactionResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountServiceMethodDescriptorSupplier("ExecuteCredit"))
              .build();
        }
      }
    }
    return getExecuteCreditMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.ReverseDebitRequest,
      ec.com.finflow.grpc.accounts.TransactionResponse> getReverseDebitMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReverseDebit",
      requestType = ec.com.finflow.grpc.accounts.ReverseDebitRequest.class,
      responseType = ec.com.finflow.grpc.accounts.TransactionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.ReverseDebitRequest,
      ec.com.finflow.grpc.accounts.TransactionResponse> getReverseDebitMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.ReverseDebitRequest, ec.com.finflow.grpc.accounts.TransactionResponse> getReverseDebitMethod;
    if ((getReverseDebitMethod = AccountServiceGrpc.getReverseDebitMethod) == null) {
      synchronized (AccountServiceGrpc.class) {
        if ((getReverseDebitMethod = AccountServiceGrpc.getReverseDebitMethod) == null) {
          AccountServiceGrpc.getReverseDebitMethod = getReverseDebitMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.accounts.ReverseDebitRequest, ec.com.finflow.grpc.accounts.TransactionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReverseDebit"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.ReverseDebitRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.TransactionResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountServiceMethodDescriptorSupplier("ReverseDebit"))
              .build();
        }
      }
    }
    return getReverseDebitMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.GetDailyTransferTotalRequest,
      ec.com.finflow.grpc.accounts.DailyTransferTotalResponse> getGetDailyTransferTotalMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetDailyTransferTotal",
      requestType = ec.com.finflow.grpc.accounts.GetDailyTransferTotalRequest.class,
      responseType = ec.com.finflow.grpc.accounts.DailyTransferTotalResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.GetDailyTransferTotalRequest,
      ec.com.finflow.grpc.accounts.DailyTransferTotalResponse> getGetDailyTransferTotalMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.GetDailyTransferTotalRequest, ec.com.finflow.grpc.accounts.DailyTransferTotalResponse> getGetDailyTransferTotalMethod;
    if ((getGetDailyTransferTotalMethod = AccountServiceGrpc.getGetDailyTransferTotalMethod) == null) {
      synchronized (AccountServiceGrpc.class) {
        if ((getGetDailyTransferTotalMethod = AccountServiceGrpc.getGetDailyTransferTotalMethod) == null) {
          AccountServiceGrpc.getGetDailyTransferTotalMethod = getGetDailyTransferTotalMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.accounts.GetDailyTransferTotalRequest, ec.com.finflow.grpc.accounts.DailyTransferTotalResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetDailyTransferTotal"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.GetDailyTransferTotalRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.DailyTransferTotalResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountServiceMethodDescriptorSupplier("GetDailyTransferTotal"))
              .build();
        }
      }
    }
    return getGetDailyTransferTotalMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.CountRecentTransfersRequest,
      ec.com.finflow.grpc.accounts.CountRecentTransfersResponse> getCountRecentTransfersMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CountRecentTransfers",
      requestType = ec.com.finflow.grpc.accounts.CountRecentTransfersRequest.class,
      responseType = ec.com.finflow.grpc.accounts.CountRecentTransfersResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.CountRecentTransfersRequest,
      ec.com.finflow.grpc.accounts.CountRecentTransfersResponse> getCountRecentTransfersMethod() {
    io.grpc.MethodDescriptor<ec.com.finflow.grpc.accounts.CountRecentTransfersRequest, ec.com.finflow.grpc.accounts.CountRecentTransfersResponse> getCountRecentTransfersMethod;
    if ((getCountRecentTransfersMethod = AccountServiceGrpc.getCountRecentTransfersMethod) == null) {
      synchronized (AccountServiceGrpc.class) {
        if ((getCountRecentTransfersMethod = AccountServiceGrpc.getCountRecentTransfersMethod) == null) {
          AccountServiceGrpc.getCountRecentTransfersMethod = getCountRecentTransfersMethod =
              io.grpc.MethodDescriptor.<ec.com.finflow.grpc.accounts.CountRecentTransfersRequest, ec.com.finflow.grpc.accounts.CountRecentTransfersResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CountRecentTransfers"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.CountRecentTransfersRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ec.com.finflow.grpc.accounts.CountRecentTransfersResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountServiceMethodDescriptorSupplier("CountRecentTransfers"))
              .build();
        }
      }
    }
    return getCountRecentTransfersMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AccountServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AccountServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AccountServiceStub>() {
        @java.lang.Override
        public AccountServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AccountServiceStub(channel, callOptions);
        }
      };
    return AccountServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AccountServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AccountServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AccountServiceBlockingStub>() {
        @java.lang.Override
        public AccountServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AccountServiceBlockingStub(channel, callOptions);
        }
      };
    return AccountServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AccountServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AccountServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AccountServiceFutureStub>() {
        @java.lang.Override
        public AccountServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AccountServiceFutureStub(channel, callOptions);
        }
      };
    return AccountServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * ============================================================
   * SERVICIO PRINCIPAL DE CUENTAS
   * ============================================================
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Obtiene información completa de una cuenta
     * </pre>
     */
    default void getAccount(ec.com.finflow.grpc.accounts.GetAccountRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.AccountResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetAccountMethod(), responseObserver);
    }

    /**
     * <pre>
     * Obtiene el balance de una cuenta (total y disponible)
     * </pre>
     */
    default void getBalance(ec.com.finflow.grpc.accounts.GetBalanceRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.BalanceResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetBalanceMethod(), responseObserver);
    }

    /**
     * <pre>
     * Verifica si una cuenta existe y está activa
     * </pre>
     */
    default void validateAccount(ec.com.finflow.grpc.accounts.ValidateAccountRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.ValidateAccountResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getValidateAccountMethod(), responseObserver);
    }

    /**
     * <pre>
     * Bloquea (reserva) un monto sin debitarlo
     * Usado antes de iniciar una transferencia
     * </pre>
     */
    default void blockAmount(ec.com.finflow.grpc.accounts.BlockAmountRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.BlockResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getBlockAmountMethod(), responseObserver);
    }

    /**
     * <pre>
     * Libera un bloqueo sin ejecutar el débito
     * Usado si la transferencia se cancela antes de procesarse
     * </pre>
     */
    default void releaseBlock(ec.com.finflow.grpc.accounts.ReleaseBlockRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.ReleaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReleaseBlockMethod(), responseObserver);
    }

    /**
     * <pre>
     * Ejecuta un débito usando un bloqueo previo
     * Convierte el monto bloqueado en débito real
     * </pre>
     */
    default void executeBlockedDebit(ec.com.finflow.grpc.accounts.ExecuteBlockedDebitRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.TransactionResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExecuteBlockedDebitMethod(), responseObserver);
    }

    /**
     * <pre>
     * Ejecuta un débito directo (sin bloqueo previo)
     * Usado para casos especiales o reversiones
     * </pre>
     */
    default void executeDebit(ec.com.finflow.grpc.accounts.ExecuteDebitRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.TransactionResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExecuteDebitMethod(), responseObserver);
    }

    /**
     * <pre>
     * Ejecuta un crédito (acredita fondos a una cuenta)
     * </pre>
     */
    default void executeCredit(ec.com.finflow.grpc.accounts.ExecuteCreditRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.TransactionResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExecuteCreditMethod(), responseObserver);
    }

    /**
     * <pre>
     * Revierte un débito previo (compensating transaction)
     * Usado si el crédito falla después del débito
     * </pre>
     */
    default void reverseDebit(ec.com.finflow.grpc.accounts.ReverseDebitRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.TransactionResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReverseDebitMethod(), responseObserver);
    }

    /**
     * <pre>
     * Obtiene el total transferido en un día (para validar límites)
     * </pre>
     */
    default void getDailyTransferTotal(ec.com.finflow.grpc.accounts.GetDailyTransferTotalRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.DailyTransferTotalResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetDailyTransferTotalMethod(), responseObserver);
    }

    /**
     * <pre>
     * Cuenta transferencias en las últimas N horas
     * </pre>
     */
    default void countRecentTransfers(ec.com.finflow.grpc.accounts.CountRecentTransfersRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.CountRecentTransfersResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCountRecentTransfersMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service AccountService.
   * <pre>
   * ============================================================
   * SERVICIO PRINCIPAL DE CUENTAS
   * ============================================================
   * </pre>
   */
  public static abstract class AccountServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return AccountServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service AccountService.
   * <pre>
   * ============================================================
   * SERVICIO PRINCIPAL DE CUENTAS
   * ============================================================
   * </pre>
   */
  public static final class AccountServiceStub
      extends io.grpc.stub.AbstractAsyncStub<AccountServiceStub> {
    private AccountServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccountServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AccountServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Obtiene información completa de una cuenta
     * </pre>
     */
    public void getAccount(ec.com.finflow.grpc.accounts.GetAccountRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.AccountResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetAccountMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Obtiene el balance de una cuenta (total y disponible)
     * </pre>
     */
    public void getBalance(ec.com.finflow.grpc.accounts.GetBalanceRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.BalanceResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetBalanceMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Verifica si una cuenta existe y está activa
     * </pre>
     */
    public void validateAccount(ec.com.finflow.grpc.accounts.ValidateAccountRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.ValidateAccountResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getValidateAccountMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Bloquea (reserva) un monto sin debitarlo
     * Usado antes de iniciar una transferencia
     * </pre>
     */
    public void blockAmount(ec.com.finflow.grpc.accounts.BlockAmountRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.BlockResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getBlockAmountMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Libera un bloqueo sin ejecutar el débito
     * Usado si la transferencia se cancela antes de procesarse
     * </pre>
     */
    public void releaseBlock(ec.com.finflow.grpc.accounts.ReleaseBlockRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.ReleaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReleaseBlockMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Ejecuta un débito usando un bloqueo previo
     * Convierte el monto bloqueado en débito real
     * </pre>
     */
    public void executeBlockedDebit(ec.com.finflow.grpc.accounts.ExecuteBlockedDebitRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.TransactionResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExecuteBlockedDebitMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Ejecuta un débito directo (sin bloqueo previo)
     * Usado para casos especiales o reversiones
     * </pre>
     */
    public void executeDebit(ec.com.finflow.grpc.accounts.ExecuteDebitRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.TransactionResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExecuteDebitMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Ejecuta un crédito (acredita fondos a una cuenta)
     * </pre>
     */
    public void executeCredit(ec.com.finflow.grpc.accounts.ExecuteCreditRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.TransactionResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExecuteCreditMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Revierte un débito previo (compensating transaction)
     * Usado si el crédito falla después del débito
     * </pre>
     */
    public void reverseDebit(ec.com.finflow.grpc.accounts.ReverseDebitRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.TransactionResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReverseDebitMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Obtiene el total transferido en un día (para validar límites)
     * </pre>
     */
    public void getDailyTransferTotal(ec.com.finflow.grpc.accounts.GetDailyTransferTotalRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.DailyTransferTotalResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetDailyTransferTotalMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Cuenta transferencias en las últimas N horas
     * </pre>
     */
    public void countRecentTransfers(ec.com.finflow.grpc.accounts.CountRecentTransfersRequest request,
        io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.CountRecentTransfersResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCountRecentTransfersMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service AccountService.
   * <pre>
   * ============================================================
   * SERVICIO PRINCIPAL DE CUENTAS
   * ============================================================
   * </pre>
   */
  public static final class AccountServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<AccountServiceBlockingStub> {
    private AccountServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccountServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AccountServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Obtiene información completa de una cuenta
     * </pre>
     */
    public ec.com.finflow.grpc.accounts.AccountResponse getAccount(ec.com.finflow.grpc.accounts.GetAccountRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetAccountMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Obtiene el balance de una cuenta (total y disponible)
     * </pre>
     */
    public ec.com.finflow.grpc.accounts.BalanceResponse getBalance(ec.com.finflow.grpc.accounts.GetBalanceRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetBalanceMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Verifica si una cuenta existe y está activa
     * </pre>
     */
    public ec.com.finflow.grpc.accounts.ValidateAccountResponse validateAccount(ec.com.finflow.grpc.accounts.ValidateAccountRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getValidateAccountMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Bloquea (reserva) un monto sin debitarlo
     * Usado antes de iniciar una transferencia
     * </pre>
     */
    public ec.com.finflow.grpc.accounts.BlockResponse blockAmount(ec.com.finflow.grpc.accounts.BlockAmountRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getBlockAmountMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Libera un bloqueo sin ejecutar el débito
     * Usado si la transferencia se cancela antes de procesarse
     * </pre>
     */
    public ec.com.finflow.grpc.accounts.ReleaseResponse releaseBlock(ec.com.finflow.grpc.accounts.ReleaseBlockRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReleaseBlockMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Ejecuta un débito usando un bloqueo previo
     * Convierte el monto bloqueado en débito real
     * </pre>
     */
    public ec.com.finflow.grpc.accounts.TransactionResponse executeBlockedDebit(ec.com.finflow.grpc.accounts.ExecuteBlockedDebitRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExecuteBlockedDebitMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Ejecuta un débito directo (sin bloqueo previo)
     * Usado para casos especiales o reversiones
     * </pre>
     */
    public ec.com.finflow.grpc.accounts.TransactionResponse executeDebit(ec.com.finflow.grpc.accounts.ExecuteDebitRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExecuteDebitMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Ejecuta un crédito (acredita fondos a una cuenta)
     * </pre>
     */
    public ec.com.finflow.grpc.accounts.TransactionResponse executeCredit(ec.com.finflow.grpc.accounts.ExecuteCreditRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExecuteCreditMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Revierte un débito previo (compensating transaction)
     * Usado si el crédito falla después del débito
     * </pre>
     */
    public ec.com.finflow.grpc.accounts.TransactionResponse reverseDebit(ec.com.finflow.grpc.accounts.ReverseDebitRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReverseDebitMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Obtiene el total transferido en un día (para validar límites)
     * </pre>
     */
    public ec.com.finflow.grpc.accounts.DailyTransferTotalResponse getDailyTransferTotal(ec.com.finflow.grpc.accounts.GetDailyTransferTotalRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetDailyTransferTotalMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Cuenta transferencias en las últimas N horas
     * </pre>
     */
    public ec.com.finflow.grpc.accounts.CountRecentTransfersResponse countRecentTransfers(ec.com.finflow.grpc.accounts.CountRecentTransfersRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCountRecentTransfersMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service AccountService.
   * <pre>
   * ============================================================
   * SERVICIO PRINCIPAL DE CUENTAS
   * ============================================================
   * </pre>
   */
  public static final class AccountServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<AccountServiceFutureStub> {
    private AccountServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccountServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AccountServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Obtiene información completa de una cuenta
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.accounts.AccountResponse> getAccount(
        ec.com.finflow.grpc.accounts.GetAccountRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetAccountMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Obtiene el balance de una cuenta (total y disponible)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.accounts.BalanceResponse> getBalance(
        ec.com.finflow.grpc.accounts.GetBalanceRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetBalanceMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Verifica si una cuenta existe y está activa
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.accounts.ValidateAccountResponse> validateAccount(
        ec.com.finflow.grpc.accounts.ValidateAccountRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getValidateAccountMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Bloquea (reserva) un monto sin debitarlo
     * Usado antes de iniciar una transferencia
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.accounts.BlockResponse> blockAmount(
        ec.com.finflow.grpc.accounts.BlockAmountRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getBlockAmountMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Libera un bloqueo sin ejecutar el débito
     * Usado si la transferencia se cancela antes de procesarse
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.accounts.ReleaseResponse> releaseBlock(
        ec.com.finflow.grpc.accounts.ReleaseBlockRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReleaseBlockMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Ejecuta un débito usando un bloqueo previo
     * Convierte el monto bloqueado en débito real
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.accounts.TransactionResponse> executeBlockedDebit(
        ec.com.finflow.grpc.accounts.ExecuteBlockedDebitRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getExecuteBlockedDebitMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Ejecuta un débito directo (sin bloqueo previo)
     * Usado para casos especiales o reversiones
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.accounts.TransactionResponse> executeDebit(
        ec.com.finflow.grpc.accounts.ExecuteDebitRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getExecuteDebitMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Ejecuta un crédito (acredita fondos a una cuenta)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.accounts.TransactionResponse> executeCredit(
        ec.com.finflow.grpc.accounts.ExecuteCreditRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getExecuteCreditMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Revierte un débito previo (compensating transaction)
     * Usado si el crédito falla después del débito
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.accounts.TransactionResponse> reverseDebit(
        ec.com.finflow.grpc.accounts.ReverseDebitRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReverseDebitMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Obtiene el total transferido en un día (para validar límites)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.accounts.DailyTransferTotalResponse> getDailyTransferTotal(
        ec.com.finflow.grpc.accounts.GetDailyTransferTotalRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetDailyTransferTotalMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Cuenta transferencias en las últimas N horas
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ec.com.finflow.grpc.accounts.CountRecentTransfersResponse> countRecentTransfers(
        ec.com.finflow.grpc.accounts.CountRecentTransfersRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCountRecentTransfersMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_ACCOUNT = 0;
  private static final int METHODID_GET_BALANCE = 1;
  private static final int METHODID_VALIDATE_ACCOUNT = 2;
  private static final int METHODID_BLOCK_AMOUNT = 3;
  private static final int METHODID_RELEASE_BLOCK = 4;
  private static final int METHODID_EXECUTE_BLOCKED_DEBIT = 5;
  private static final int METHODID_EXECUTE_DEBIT = 6;
  private static final int METHODID_EXECUTE_CREDIT = 7;
  private static final int METHODID_REVERSE_DEBIT = 8;
  private static final int METHODID_GET_DAILY_TRANSFER_TOTAL = 9;
  private static final int METHODID_COUNT_RECENT_TRANSFERS = 10;

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
        case METHODID_GET_ACCOUNT:
          serviceImpl.getAccount((ec.com.finflow.grpc.accounts.GetAccountRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.AccountResponse>) responseObserver);
          break;
        case METHODID_GET_BALANCE:
          serviceImpl.getBalance((ec.com.finflow.grpc.accounts.GetBalanceRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.BalanceResponse>) responseObserver);
          break;
        case METHODID_VALIDATE_ACCOUNT:
          serviceImpl.validateAccount((ec.com.finflow.grpc.accounts.ValidateAccountRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.ValidateAccountResponse>) responseObserver);
          break;
        case METHODID_BLOCK_AMOUNT:
          serviceImpl.blockAmount((ec.com.finflow.grpc.accounts.BlockAmountRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.BlockResponse>) responseObserver);
          break;
        case METHODID_RELEASE_BLOCK:
          serviceImpl.releaseBlock((ec.com.finflow.grpc.accounts.ReleaseBlockRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.ReleaseResponse>) responseObserver);
          break;
        case METHODID_EXECUTE_BLOCKED_DEBIT:
          serviceImpl.executeBlockedDebit((ec.com.finflow.grpc.accounts.ExecuteBlockedDebitRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.TransactionResponse>) responseObserver);
          break;
        case METHODID_EXECUTE_DEBIT:
          serviceImpl.executeDebit((ec.com.finflow.grpc.accounts.ExecuteDebitRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.TransactionResponse>) responseObserver);
          break;
        case METHODID_EXECUTE_CREDIT:
          serviceImpl.executeCredit((ec.com.finflow.grpc.accounts.ExecuteCreditRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.TransactionResponse>) responseObserver);
          break;
        case METHODID_REVERSE_DEBIT:
          serviceImpl.reverseDebit((ec.com.finflow.grpc.accounts.ReverseDebitRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.TransactionResponse>) responseObserver);
          break;
        case METHODID_GET_DAILY_TRANSFER_TOTAL:
          serviceImpl.getDailyTransferTotal((ec.com.finflow.grpc.accounts.GetDailyTransferTotalRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.DailyTransferTotalResponse>) responseObserver);
          break;
        case METHODID_COUNT_RECENT_TRANSFERS:
          serviceImpl.countRecentTransfers((ec.com.finflow.grpc.accounts.CountRecentTransfersRequest) request,
              (io.grpc.stub.StreamObserver<ec.com.finflow.grpc.accounts.CountRecentTransfersResponse>) responseObserver);
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
          getGetAccountMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.accounts.GetAccountRequest,
              ec.com.finflow.grpc.accounts.AccountResponse>(
                service, METHODID_GET_ACCOUNT)))
        .addMethod(
          getGetBalanceMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.accounts.GetBalanceRequest,
              ec.com.finflow.grpc.accounts.BalanceResponse>(
                service, METHODID_GET_BALANCE)))
        .addMethod(
          getValidateAccountMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.accounts.ValidateAccountRequest,
              ec.com.finflow.grpc.accounts.ValidateAccountResponse>(
                service, METHODID_VALIDATE_ACCOUNT)))
        .addMethod(
          getBlockAmountMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.accounts.BlockAmountRequest,
              ec.com.finflow.grpc.accounts.BlockResponse>(
                service, METHODID_BLOCK_AMOUNT)))
        .addMethod(
          getReleaseBlockMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.accounts.ReleaseBlockRequest,
              ec.com.finflow.grpc.accounts.ReleaseResponse>(
                service, METHODID_RELEASE_BLOCK)))
        .addMethod(
          getExecuteBlockedDebitMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.accounts.ExecuteBlockedDebitRequest,
              ec.com.finflow.grpc.accounts.TransactionResponse>(
                service, METHODID_EXECUTE_BLOCKED_DEBIT)))
        .addMethod(
          getExecuteDebitMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.accounts.ExecuteDebitRequest,
              ec.com.finflow.grpc.accounts.TransactionResponse>(
                service, METHODID_EXECUTE_DEBIT)))
        .addMethod(
          getExecuteCreditMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.accounts.ExecuteCreditRequest,
              ec.com.finflow.grpc.accounts.TransactionResponse>(
                service, METHODID_EXECUTE_CREDIT)))
        .addMethod(
          getReverseDebitMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.accounts.ReverseDebitRequest,
              ec.com.finflow.grpc.accounts.TransactionResponse>(
                service, METHODID_REVERSE_DEBIT)))
        .addMethod(
          getGetDailyTransferTotalMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.accounts.GetDailyTransferTotalRequest,
              ec.com.finflow.grpc.accounts.DailyTransferTotalResponse>(
                service, METHODID_GET_DAILY_TRANSFER_TOTAL)))
        .addMethod(
          getCountRecentTransfersMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ec.com.finflow.grpc.accounts.CountRecentTransfersRequest,
              ec.com.finflow.grpc.accounts.CountRecentTransfersResponse>(
                service, METHODID_COUNT_RECENT_TRANSFERS)))
        .build();
  }

  private static abstract class AccountServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    AccountServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return ec.com.finflow.grpc.accounts.AccountsProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("AccountService");
    }
  }

  private static final class AccountServiceFileDescriptorSupplier
      extends AccountServiceBaseDescriptorSupplier {
    AccountServiceFileDescriptorSupplier() {}
  }

  private static final class AccountServiceMethodDescriptorSupplier
      extends AccountServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    AccountServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (AccountServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AccountServiceFileDescriptorSupplier())
              .addMethod(getGetAccountMethod())
              .addMethod(getGetBalanceMethod())
              .addMethod(getValidateAccountMethod())
              .addMethod(getBlockAmountMethod())
              .addMethod(getReleaseBlockMethod())
              .addMethod(getExecuteBlockedDebitMethod())
              .addMethod(getExecuteDebitMethod())
              .addMethod(getExecuteCreditMethod())
              .addMethod(getReverseDebitMethod())
              .addMethod(getGetDailyTransferTotalMethod())
              .addMethod(getCountRecentTransfersMethod())
              .build();
        }
      }
    }
    return result;
  }
}
