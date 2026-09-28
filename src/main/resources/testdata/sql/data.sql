-- One row = one execution of CurrencyConverterDataDrivenTest. Add a row to add a variation; set active = FALSE to skip one.
MERGE INTO currency_conversion (currency_code, currency_name, amount, active)
    KEY (currency_code, amount)
    VALUES ('USD', '1 აშშ დოლარი', 250.00, TRUE),
           ('EUR', '1 ევრო', 100.00, TRUE),
           ('GBP', '1 გირვანქა სტერლინგი', 75.00, TRUE),
           ('CHF', '1 შვეიცარული ფრანკი', 40.00, TRUE);
