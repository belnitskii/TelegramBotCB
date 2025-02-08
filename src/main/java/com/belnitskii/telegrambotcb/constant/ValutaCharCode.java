package com.belnitskii.telegrambotcb.constant;

import lombok.Getter;

@Getter
public enum ValutaCharCode {
    AUD("R01010", "Австралийский доллар"),
    AZN("R01020A", "Азербайджанский манат"),
    GBP("R01035", "Фунт стерлингов"),
    AMD("R01060", "Армянских драмов"),
    BYN("R01090B", "Белорусский рубль"),
    BGN("R01100", "Болгарский лев"),
    BRL("R01115", "Бразильский реал"),
    HUF("R01135", "Форинтов"),
    VND("R01150", "Донгов"),
    HKD("R01200", "Гонконгский доллар"),
    GEL("R01210", "Лари"),
    DKK("R01215", "Датская крона"),
    AED("R01230", "Дирхам ОАЭ"),
    USD("R01235", "Доллар США"),
    EUR("R01239", "Евро"),
    EGP("R01240", "Египетских фунтов"),
    INR("R01270", "Индийских рупий"),
    IDR("R01280", "Рупий"),
    KZT("R01335", "Тенге"),
    CAD("R01350", "Канадский доллар"),
    QAR("R01355", "Катарский риал"),
    KGS("R01370", "Сомов"),
    CNY("R01375", "Юань"),
    MDL("R01500", "Молдавских леев"),
    NZD("R01530", "Новозеландский доллар"),
    NOK("R01535", "Норвежских крон"),
    PLN("R01565", "Злотый"),
    RON("R01585F", "Румынский лей"),
    XDR("R01589", "СДР (специальные права заимствования)"),
    SGD("R01625", "Сингапурский доллар"),
    TJS("R01670", "Сомони"),
    THB("R01675", "Батов"),
    TRY("R01700J", "Турецких лир"),
    TMT("R01710A", "Новый туркменский манат"),
    UZS("R01717", "Узбекских сумов"),
    UAH("R01720", "Гривен"),
    CZK("R01760", "Чешских крон"),
    SEK("R01770", "Шведских крон"),
    CHF("R01775", "Швейцарский франк"),
    RSD("R01805F", "Сербских динаров"),
    ZAR("R01810", "Рэндов"),
    KRW("R01815", "Вон"),
    JPY("R01820", "Иен");

    public final String code;
    public final String name;

    ValutaCharCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static boolean Contain(String charCode) {
        for (ValutaCharCode value : ValutaCharCode.values()) {
            if (value.name().equals(charCode)) {
                return true;
            }
        }
        return false;
    }


}
