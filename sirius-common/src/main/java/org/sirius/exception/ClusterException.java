package org.sirius.exception;

public class ClusterException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6876782389045650272L;

	public ClusterException(ErrorCode errorCode) {
		super(errorCode.getCode() + ":" + errorCode.getDesc());
	}

	public ClusterException(ErrorCode errorCode, Throwable t) {
		super(errorCode.getCode() + ":" + errorCode.getDesc(), t);
	}

}
