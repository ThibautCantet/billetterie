package com.backofficebilletterie.controller;

public record OrderRequest(String cartId, String amount) {
}
