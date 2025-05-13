package com.bank.controller;

public record TransactionResponse(String id, String status, String redirectionUrl) {
}
