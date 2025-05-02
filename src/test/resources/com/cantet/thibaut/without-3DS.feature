# language: fr
  Fonctionnalité: Paiment sans 3DS

    Scénario: Validation du paiement sans 3DS OK et transformation du panier en commande OK
        Etant donné un panier "1234567890123456" de 100 euros
        Et des information de paiement suivant numéro de carte "1234567890123456"
        Et une date d'expiration "12/27" et un cryptogramme "123"
        Et que la banque valide le paiement "324234243234" sans 3DS
        Et que le panier "1234567890123456" est transformé en commande "1234567890123456"
        Quand on valide le paiement
        Alors on obtient une commande "1234567890123456" d'un montant de 100 euros
