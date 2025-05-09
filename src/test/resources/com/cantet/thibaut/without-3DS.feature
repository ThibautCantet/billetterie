# language: fr
  Fonctionnalité: Paiment sans 3DS

    Scénario: Validation du paiement sans 3DS KO, on reste sur le panier
        Etant donné un panier "1234567890123456" de 100 euros
        Et des information de paiement suivant numéro de carte "1234567890123456"
        Et une date d'expiration "12/27" et un cryptogramme "123"
        Et que la banque ne valide pas le paiement "324234243234" sans 3DS
        Quand on valide le paiement
        Alors on reste le panier "1234567890123456" de 100 euros
        Et le panier "1234567890123456" n'a pas été transformé en commande

    Scénario: Validation du paiement sans 3DS OK, tranformation du panier en commande KO, on reste sur le panier
        Etant donné un panier "1234567890123456" de 100 euros
        Et des information de paiement suivant numéro de carte "1234567890123456"
        Et une date d'expiration "12/27" et un cryptogramme "123"
        Et que la banque valide le paiement "324234243234" sans 3DS
        Et que le panier "1234567890123456" n'est pas transformé en commande
        Et que on la transaction bancaire "324234243234" est annulée
        Quand on valide le paiement
        Alors on reste le panier "1234567890123456" de 100 euros
        Et on a bien annulé la transaction bancaire "324234243234"

    Scénario: Validation du paiement sans 3DS OK et transformation du panier en commande OK
        Etant donné un panier "1234567890123456" de 100 euros
        Et des information de paiement suivant numéro de carte "1234567890123456"
        Et une date d'expiration "12/27" et un cryptogramme "123"
        Et que la banque valide le paiement "324234243234" sans 3DS
        Et que le panier "1234567890123456" est transformé en commande "1234567890123456"
        Quand on valide le paiement
        Alors on obtient une commande "1234567890123456" d'un montant de 100 euros avec la transaction bancaire "324234243234"
