[![Build Status](https://ci.inria.fr/zone/job/Zone-run-tests/badge/icon)](https://ci.inria.fr/zone/job/Zone-run-tests/)

# ZONE-Project
#### A semantic RSS feed aggregator

ZONE-project, give us a new approach of RSS feeds. True news monitoring tool, the software has a greater aggregation capacity than traditional RSS Readers combining the power of the semantic web.
The informations received by the user are filtered according to several criteria, which makes him more relevant and efficient. For business usage of public usage, the application can meet their specific needs. 

#### Demonstration
Go to our [demonstration](http://demo.zone-project.org) page, and clic on some filters.

#### Run your own server

If you want to have your own aggregating service:
Install virtuoso and Rails, configure the ZONE-extractor/config/zone.properties, ZONE-watcher/config/database.yml and ZONE-watcher/config/config.yml

    git clone https://github.com/descl/ZONE
    cd ZONE-extractor
    mvn install
    mvn exec:java -pl ZONE-extractor-start&
    cd ../ZeOntologyNewsExtractorUserRunner
    rails s
    go to http://localhost:3000/

[more infos on install](https://github.com/descl/ZONE/wiki/install)

[contribute](https://github.com/descl/ZONE/wiki/contribute)


## Licenses

The program can be used under the terms of the [GNU Affero General Public License , 3.0](http://www.gnu.org/licenses/agpl-3.0.html).

The documentation on this website is shared as [Creative Commons Attribution-ShareAlike 3.0 Unported License](http://en.wikipedia.org/wiki/Wikipedia:Text_of_Creative_Commons_Attribution-ShareAlike_3.0_Unported_License)

## Team

#### Core devs
[Christophe Desclaux](http://desclaux.me) (Polytech'Nice-Sophia, INRIA), from the begining (, to the end)

#### Collaborators
[Blay-Fornarino Mireille](http://users.polytech.unice.fr/~blay/) - Managment of the christophe's end year project and help for papers publication

Bouaziz Ameni - work on SVM integration

El Fouzi Ilhame - work on clustering technologies

[Faron Zucker](http://www.i3s.unice.fr/~faron/) Catherine - Managment of the christophe's end year project

Urli Simon - co-author of paper "constructing workflows for news semantic filtering"


## Acknowledgements
This work has been partially funded by:
  * [INRIA](http://inria.fr), as a one year full time job for the core dev in the context of [BoostYourCode contest](http://www.inria.fr/actualite/actualites-inria/boost-your-code-2012), Nov 2012 - Nov 2013
