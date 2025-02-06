package com.belnitskii.telegrambotcb.constant;

public enum ValutaCharCode {
    AUD("R01010"),
    AZN("R01020A"),
    GBP("R01035"),
    AMD("R01060"),
    BYN("R01090B"),
    BGN("R01100"),
    BRL("R01115"),
    HUF("R01135"),
    VND("R01150"),
    HKD("R01200"),
    GEL("R01210"),
    DKK("R01215"),
    AED("R01230"),
    USD("R01235"),
    EUR("R01239"),
    EGP("R01240"),
    INR("R01270"),
    IDR("R01280"),
    KZT("R01335"),
    CAD("R01350"),
    QAR("R01355"),
    KGS("R01370"),
    CNY("R01375"),
    MDL("R01500"),
    NZD("R01530"),
    NOK("R01535"),
    PLN("R01565"),
    RON("R01585F"),
    XDR("R01589"),
    SGD("R01625"),
    TJS("R01670"),
    THB("R01675"),
    TRY("R01700J"),
    TMT("R01710A"),
    UZS("R01717"),
    UAH("R01720"),
    CZK("R01760"),
    SEK("R01770"),
    CHF("R01775"),
    RSD("R01805F"),
    ZAR("R01810"),
    KRW("R01815"),
    JPY("R01820");

    public final String code;

    ValutaCharCode(String code) {
        this.code = code;
    }
}
