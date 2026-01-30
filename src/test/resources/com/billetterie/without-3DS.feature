# language: fr
  Fonctionnalité: Paiement sans 3DS

    Scénario: Validation du paiement sans 3DS KO, on reste sur le paiement
        Etant donné un panier "1234567890123456" de 100 euros pour "client@mail.com"
        Et des information de paiement suivant numéro de carte "1234567890123456"
        Et une date d'expiration "12/27" et un cryptogramme "123"
        Et que la banque ne valide pas le paiement "324234243234" sans 3DS
        Quand on valide le paiement
        Alors on reste sur le paiement "1234567890123456" de 100 euros
        Et le panier "1234567890123456" n'a pas été transformé en commande

    Scénario: Validation du paiement sans 3DS OK, transformation du panier en commande KO, on reste sur le paiement et annulation de la transaction bancaire OK
        Etant donné un panier "1234567890123456" de 100 euros pour "client@mail.com"
        Et des information de paiement suivant numéro de carte "1234567890123456"
        Et une date d'expiration "12/27" et un cryptogramme "123"
        Et que la banque valide le paiement "324234243234" sans 3DS
        Et que le panier "1234567890123456" n'est pas transformé en commande
        Et que on la transaction bancaire "324234243234" est annulée
        Quand on valide le paiement
        Alors on reste sur le paiement "1234567890123456" de 100 euros
        Et on a bien annulé la transaction bancaire "324234243234"

    Scénario: Validation du paiement sans 3DS OK, transformation du panier en commande KO, on revient sur le paiement,
    l'annulation de la transaction bancaire KO et on notifie le support client qu'il faut annuler la transaction bancaire à la main
      Etant donné un panier "1234567890123456" de 100 euros pour "client@mail.com"
      Et des information de paiement suivant numéro de carte "1234567890123456"
      Et une date d'expiration "12/27" et un cryptogramme "123"
      Et que la banque valide le paiement "324234243234" sans 3DS
      Et que le panier "1234567890123456" n'est pas transformé en commande
      Et que on la transaction bancaire "324234243234" n'est pas annulée
      Quand on valide le paiement
      Et le support client est notifié qu'il faut annuler la transaction bancaire "324234243234" à la main

    Scénario: Validation du paiement sans 3DS OK et transformation du panier en commande OK
        Etant donné un panier "1234567890123456" de 100 euros pour "client@mail.com"
        Et des information de paiement suivant numéro de carte "1234567890123456"
        Et une date d'expiration "12/27" et un cryptogramme "123"
        Et que la banque valide le paiement "324234243234" sans 3DS
        Et que le panier "1234567890123456" est transformé en commande "1234567890123456"
        Quand on valide le paiement
        Alors on est bien redirigé vers la page de confirmation de commande "1234567890123456" d'un montant de 100 euros avec une transaction bancaire "324234243234"
        Et on obtient une commande "1234567890123456" d'un montant de 100 euros
        Et un email de confirmation est envoyé à "client@mail.com" pour la commande "1234567890123456" d'un montant de 100 euros
