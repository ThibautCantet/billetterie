# language: fr
  Fonctionnalité: Paiment avec 3DS

    Scénario: Validation du paiement avec 3DS OK, tranformation du panier en commande KO, on revient sur le paiement,
        et annulation de la transaction bancaire OK
        Etant donné un panier "1234567890123456" de 100 euros
        Et des information de paiement suivant numéro de carte "1234567890123456"
        Et une date d'expiration "12/27" et un cryptogramme "123"
        Et que la banque valide le paiement "324234243234" avec 3DS
        Et que le panier "1234567890123456" n'est pas transformé en commande
        Et que on la transaction bancaire "324234243234" est annulée
        Quand on revient sur la billetterie avec la transaction bancaire "324234243234"
        Alors on revient sur le paiement avec le panier "1234567890123456" de 100 euros
        Et on a bien annulé la transaction bancaire "324234243234"

    Scénario: Validation du paiement avec 3DS OK, tranformation du panier en commande KO, on revient sur le paiement,
        l'annulation de la transaction bancaire KO et on notifie le support client qu'il faut annuler la transaction bancaire à la main
        Etant donné un panier "1234567890123456" de 100 euros
        Et des information de paiement suivant numéro de carte "1234567890123456"
        Et une date d'expiration "12/27" et un cryptogramme "123"
        Et que la banque valide le paiement "324234243234" avec 3DS
        Et que le panier "1234567890123456" n'est pas transformé en commande
        Et que on la transaction bancaire "324234243234" n'est pas annulée
        Quand on revient sur la billetterie avec la transaction bancaire "324234243234"
        Alors on revient sur le paiement avec le panier "1234567890123456" de 100 euros
        Et le support client est notifié qu'il faut annuler la transaction bancaire "324234243234" à la main

    Scénario: Validation du paiement avec 3DS OK et transformation du panier en commande OK
        Etant donné un panier "1234567890123456" de 100 euros
        Et des information de paiement suivant numéro de carte "1234567890123456"
        Et une date d'expiration "12/27" et un cryptogramme "123"
        Et que la banque valide le paiement "324234243234" avec 3DS
        Et que le panier "1234567890123456" est transformé en commande "1234567890123456"
        Quand on revient sur la billetterie avec la transaction bancaire "324234243234"
        Alors on obtient une commande "1234567890123456" d'un montant de 100 euros
