# language: fr
  Fonctionnalité: Panier réserve : Paiement sans 3DS

    Scénario: Validation du paiement sans 3DS KO, on reste sur le paiement
        Etant donné un panier réservé "4535131265464321" de 100 euros
        Et des information de paiement suivant numéro de carte "4535131265464321"
        Et une date d'expiration "12/27" et un cryptogramme "123"
        Et que la banque ne valide pas le paiement "324234243234" sans 3DS
        Quand on valide le paiement
        Alors on reste sur le paiement "4535131265464321" de 100 euros
        Et le panier "4535131265464321" n'a pas été transformé en commande

    Scénario: Validation du paiement sans 3DS OK, transformation du panier en commande KO, on reste sur le paiement
        Etant donné un panier réservé "4535131265464321" de 100 euros
        Et des information de paiement suivant numéro de carte "4535131265464321"
        Et une date d'expiration "12/27" et un cryptogramme "123"
        Et que la banque valide le paiement "324234243234" sans 3DS
        Et que le panier "4535131265464321" n'est pas transformé en commande
        Et que on la transaction bancaire "324234243234" est annulée
        Quand on valide le paiement
        Alors on reste sur le paiement "4535131265464321" de 100 euros
        Et on a bien annulé la transaction bancaire "324234243234"

    Scénario: Validation du paiement du panier réservé sans 3DS OK et transformation du panier réservé en commande OK
        Etant donné un panier réservé "4535131265464321" de 100 euros
        Et des information de paiement suivant numéro de carte "4535131265464321"
        Et une date d'expiration "12/27" et un cryptogramme "123"
        Et que la banque valide le paiement "324234243234" sans 3DS
        Et que le panier "4535131265464321" est transformé en commande "4535131265464321"
        Quand on valide le paiement
        Alors on est bien redirigé vers la page des commandes avec la commande "4535131265464321" d'un montant de 100 euros avec une transaction bancaire "324234243234"
        Et on obtient une commande "4535131265464321" d'un montant de 100 euros sur la page des commandes
