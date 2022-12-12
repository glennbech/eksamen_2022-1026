# Del 1 - DevOps prinsipper

## Oppgave 1

Det er flere ting som er problematisk med hvordan systemutviklingsprosessen foregår nå, og hvordan det påvirker fremdrift og utvikling. For det første så er det et stort problem at kode blir deployet så skjeldent.
Det er mye som kan endre seg fra kvartal til kvartal. Man risikerer å produsere waste som er bortkastet ressurser. Det er lurere å ha en hyppigere deployment av kode så man stadig kan få tilbakemelding om det som blir levert er på rett kjøl og man kun produsrer det som er nødvendig.
Det at det ble mye feil, da de hadde en hyppigere deployment av kode har jo egentlig ikke endret seg noe særlig ved å senke antall deployments som blir gjennomført. Derfor er det lurere og komme seg tilbake til å deploye oftere.
En annen fordel med en hyppig deployment er at om en feil kommer seg gjennom beskyttelsestiltakene vil det å rulle tilbake til en tidligere versjon være mindre synlig da den forrige versjon vil kunne ha mye av samme funksjonalitet som den nyeste.
Det vil også være raskt å kunne Men det er samtidig viktig å få på plass strukturen og sette opp systemet så man unngår at feil skjer. Dette kan gjøres blant annet ved å implementere branch protection slik at kun kode som har passert alle tester vil kunne publiseres.
Det vil også være lurt å implementere automatisering ved bruk av terraform og workflows.


At det ikke benyttes automatisk deployment til sky men benytting av zippet jar fil og FTP, dette er tidkrevende og mye manuelt arbeid som er helt unødvendig.
Dette er et ledd som burde fjernes og skiftes ut med automatisk deployment til en skyløsning som f.eks AWS.
Dette kan fint bli gjort ved bruk av GitHub Actions med workflows og docker, samt terraform.

At det releases kode ofte kan påføre et press utviklingsteamet om å holde fremdriften over tid og stadig klare å levere. Det kan fort følge med feil i kode da det ikke alltid er god nok tid til å se gjennom og benytte reviews i god nok grad.
Om man har implementert god struktur i systemt så vil det hjelpe prosessen masse og det vil være lettere å takle fremdriften og redusere risikoen ved hyppige realises.

# Del 2 - CI

## Oppgave 3

Det sensor må gjøre av konfigurasjoner på sin fork er:

### Ingen kan pushe kode direkte på main branch

- Gå inn i Settings på hovedsiden av repositoriet i GitHub.
- I sidepanelet (Venstre side) velg Branchces.
- Trykk så på Add branch protection rule. Dette vil vise en liste over mulige regler som kan angis.
- Under Branch name pattern så skal main eller master skrives inn, avhengig av hva man har som
hovedbranch. Dette spesifiserer hvilken branch vi ønsker at regelen vi holder på med skal gjelde
for.
- For at man ikke skal kunne pushe direkter til main, så huker vi av på Require a pull request
before merging. Dette gjør at en commit må gjøres mot en branch som ikke er beskyttet.
- Huk også av på Do not allow bypassing the aboce settings. Fir at admin og andre med tilpassede
roller skal kunne snike seg unne regelen.

### Kode kan merges til main branch ved å lage en Pull request med minst en godkjenning.

- Etter å ha huket av på Require a pull request before merging så vil man ha muligheten til å huke
av Require approvals. Dette gjør at en pull request må ha godkjenning fra andre for å fullføres.
Man vil også ha muligheten til å velge hvor mange godkjenninger som trengs ved å benytte
nedtrekksmenyen.

### Kode kan merges til main bare når feature branchen som pull requesten er basert på, er verifisert av GitHub Actions

- For at kode skal bli verifisert av GitHub Actions så huker man av på Require status checks to
pass before merging og undervalget Require branches to be up to date before merging.
- I tekstfeltet under dette valget så skriver man inn build og trykker på valget som dukker opp.

# Del 3 - Docker

## Oppgave 1

For å få workfloen til å fungere med egen Docker Hub konto må man endre litt på docker.yml. For å se at
den bygger uten å fjerne branch protection er det lettest under testing av workfloen å fjerne main/ master
fra hvilken branch som skal lytte til pushes. (on: push: ). Når vi nå gjennomfører en commit vil vi se at
Docker build starter. Det som nå er problemet er at den feiler. Om man trykker på workfloen og så kan man
se trinnene den gjennomfører. Her ser vi at den får en error når den prøver å logge seg inn i Docker Hub.
Det er ikke rart da vi ikke har angitt noen Repository Secret enda. For å lage disse gjør man følgende:
- Fra hovedsiden i repositoriet gå til Settings.
- I sidepanelet til venstre trykker man på Secrets som ligger under Security og så trykk på Actions.
- Man kan nå lage et Repository Secret ved å trykke på New repository secret.
- I docker.yml kan vi se at vi har DOCKER_HUB_USERNAME og DOCKER_HUB_TOKEN. Når vi
oppretter en Repository Secret så er det disse som må benytte som navn til hemmeligehetene.
- I feltet nedenfor kan man legge inn tekst. Her skal man på DOCKER_HUB_USERNAME legge til sitt
brukernavn for sin Docker Hub konto og på DOCKER_HUB_TOKEN legge til sitt passord. Disse vil
nå være trygt lagret og kan ikke sees av andre. Om man prøver å radigere en hemmelighet har man
ikke mulighet til å se eksisterende verdi som er lagt inn, men kun å endre navn og sette ny verdi.
Når vi nå prøver å kjøre workfloen ser vi at den fungerer som den skal, og passerer.


## Oppgave 3

Det sensor må gjøre for å få sin fork til å fungere og laste opp til sitt eget ECR repo er å først legge til
AWS_ACCESS_KEY_ID og AWS_SECRET_ACCESS_KEY i GitHub på samme måte som ble benyttet ved å
legge til for Docker Hub innloggings detaljer. For å finne AWS_ACCESS_KEY_ID og AWS_SECRET_ACCESS_KEY
så må sensor gjøre følgende:
- I AWS sitt søkefelt på toppen, søke på IAM.
- Etter å ha valgt IAM skal man velge Users fra valgmulighetene på venstre side.
- Man vil så få opp en liste med alle brukere. Her ma man så finne sin egen bruker ved å lete på listen.
Man kan også benytte søkefeltet for å raskere og lettere finne ønsket bruker. Når man har funnet
ønsket bruker klikker man på navnet.
- Når man har kommet inn på ønsket bruker velger man fanen Security credentials.
- Så velger man Create access key.
- Bokstav og tallkombinasjonen under Access key ID skal vi legge til i GitHub under Repository Secret
som vi gjorde tidligere.
- Under Secret access key så trykker man på Show. Dette vil vise en tall og tekstkombinasjon som må
kopieres inn i Repository Secret på samme måte som med Access key ID.
- I AWS så må det nå opprettes ett ECR repo. Dette kan gjøres ved å søke på ECR i toppfeltet og velge
Elastic Container Registry.
- Helt til høyre må man så trykke på Create repository. Den siden man så kommer til lar deg legge
til og endre noen innstillinger. Det eneste som trengs å gjøres her er å opprette et navn for repoet. I
dette tilfellet har vi benyttet et navn som skal være det samme som kandidatnummeret vårt på denne
eksamenen.
- I bunn av siden klikker man så på Create repository. Og man har nå opprettet ECR repoet sitt. Dette
kan man finne ved å bla i listen over alle repositories eller å benytte søkefeltet til å finne ønsket repo.
Det som så må gjøres er å endre litt på docker.yml filen.
- På linje 27 og 28, der det er skrevet
docker tag $rev 244530008913.dkr.ecr.eu-west-1.amazonaws.com/1026:$rev
og
docker push 244530008913.dkr.ecr.eu-west-1.amazonaws.com/1026:$rev
må man bytte ut 1026 det navnet man ga sitt ECR repo tidligere.

# Del 5 - Terraform og CloudWatch Dashboards

## Oppgave 1

Årsaken til dette er at en bucket kan kun ha en av et navn, og det navnet må være globalt unikt.
Slik koden er per dags dato er så præver de å opprette ny bucket hver eneste gang, og kun første gang fungerte da det ikke var noen bucket fra før av.
Grunnen til at den prøver å gjøre det er nok fordi "minne" filen ikke har blitt opprettet på korrekt måte. Terraform er derfor ikke klar over at det allerede finnes en bucket,
og tror det derfor er grunn til å lage en bucket.

# Egne tanker

Fikk til en god del av oppgavene, men slet med å få terraform til å spille på lag. Vikk opp feilmeling om at det ikke fantes noen s3 bucket og derfor ville ikke workflowen gjennomføres.
Dette var veldig irriterende å ikke få til, da jeg føler det har gått greit på øvinger.

Brukte for mye tid på å få metrics til å fungere, og er ikke sikker på om jeg fikk "checkout_latency" til å fungere da det til tider tok lang tid før ny matrics
ble synlig i CloudWatch, fikk derfor ikke dobbeltsjekket om den kom opp korrekt.