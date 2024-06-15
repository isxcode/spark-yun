package com.isxcode.star.agent.controller;

import com.isxcode.star.agent.grpc.ExecuteWorkRpcReq;
import com.isxcode.star.agent.grpc.ExecuteWorkRpcRes;
import com.isxcode.star.agent.grpc.GreeterGrpc;
import io.grpc.stub.StreamObserver;

public class AgentServiceRpc extends GreeterGrpc.GreeterImplBase {

	@Override
	public void executeWork(ExecuteWorkRpcReq request, StreamObserver<ExecuteWorkRpcRes> responseObserver) {

		ExecuteWorkRpcRes reply = ExecuteWorkRpcRes.newBuilder().setAppId("Hello ispong").build();

		responseObserver.onNext(reply);

		responseObserver.onCompleted();
	}
}