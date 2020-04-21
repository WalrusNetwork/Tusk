#!/bin/bash
cd "$(dirname "$0")"

# Helper function for urlencoding variables
urlencode() {
	local string="${1}"
	local strlen=${#string}
	local encoded=""
	local pos c o

	for (( pos=0 ; pos<strlen ; pos++ )); do
		c=${string:$pos:1}
		case "$c" in
			[-_.~a-zA-Z0-9] ) o="${c}" ;;
			* )               printf -v o '%%%02x' "'$c"
		esac
		encoded+="${o}"
	done
	echo "${encoded}"
	RETURN="${encoded}"
}

# Grab credentials
if [ "$#" -eq 4 ]; then
	GU="$1"
	GP="$2"
	MU="$3"
	MP="$4"
else
	printf "Usage:\t$0\n\t$0 <gitlab-user> <gitlab-pass> <maven-user> <maven-pass>\n\nEnsure your password is properly escaped if passed on the command line.\n\n"
	printf "GitLab Username: "
	read GU
	stty -echo
	printf "GitLab Password: "
	read GP
	stty echo
	printf "\nMaven Username: "
	read MU
	stty -echo
	printf "Maven Password: "
	read MP
	stty echo
	echo ""
fi
GUE="$(urlencode ${GU})"
GPE="$(urlencode ${GP})"

# Detect OS and install required system packages
if [ -n "$(command -v apt-get)" ]; then
	echo "Detected Debian-like OS"
	apt-get -y update
	apt-get -y install curl git openjdk-8-jre maven screen wget
else
	if [ -n "$(command -v yum)" ]; then
		echo "Detected RHEL-like OS"
		yum -y update
	    yum -y install curl git openjdk-8-jre maven screen wget
	else
		if [ -n "$(command -v )" ]; then
		    echo "Detected Arch-like OS"
		    pacman -Sy curl git jre8-openjdk maven screen wget
		else
    		echo "[*] Unsupported OS!"
    		exit 1
		fi
	fi
fi

# Add walrus user
if ! id walrus > /dev/null 2>&1; then
	useradd walrus
fi

HOME="$(getent passwd "walrus" | cut -d: -f6)"
ROOT="${HOME}/devserver"

# Establish settings file
cat <<EOT > "${HOME}/.m2/settings.xml"
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
		https://maven.apache.org/xsd/settings-1.0.0.xsd">
	<localRepository>${HOME}/.m2/repository</localRepository>
	<interactiveMode>true</interactiveMode>
	<offline>false</offline>
	<servers>
		<server>
			<id>walrus-private</id>
			<username>${MU}</username>
			<password>${MP}</password>
		</server>
	</servers>
</settings>
EOT
chown walrus:walrus "${HOME}/.m2/settings.xml"

# Run remaining commands as walrus user
su - walrus <<'EOF'
cd ~
HOME="$(pwd)"

# Initialize directory & file structure
mkdir -p devserver
cd devserver
ROOT="$(pwd)"
echo "eula=true" > "${ROOT}/eula.txt"
mkdir -p "${ROOT}/plugins/GameManager/components"
git config --global user.email "dev@walrus.gg"
git config --global user.name "Walrus Developer"
mkdir -p ~/.m2

# Clone and build SportPaper
echo "Fetching SportPaper (this will take a few minutes!)..."
git clone https://gitlab.com/WalrusNetwork/minecraft/sportpaper
"${ROOT}/sportpaper/sportpaper" build

sportpath="$(find ${ROOT}/sportpaper/SportPaper-Server/ -name 'sportpaper*.jar')"
if [ "${sportpath}" == "" ] || [ ! -f "${sportpath}" ]; then
	echo "[*] It appears sportpaper had a problem being created! Please correct the errors and run this again."
	exit 1
fi
ln -s "${sportpath}" sportpaper.jar

echo "Initializing server instance..."
screen -dmS SportPaper bash -c "java -jar '${ROOT}/sportpaper.jar'"
sleep 1
screen -S SportPaper -X stuff "stop
"

# Clone and build Walrus Minecraft plugins
echo "Fetching Walrus repository..."
EOF
su - walrus <<EOF
cd ~
HOME="$(pwd)"
ROOT="${HOME}/devserver"
cd "${ROOT}"
git clone "https://${GUE}:${GPE}@gitlab.com/WalrusNetwork/minecraft/walrus"
EOF
su - walrus <<'EOF'
cd ~
HOME="$(pwd)"
ROOT="${HOME}/devserver"
cd "${ROOT}"

cd "${ROOT}/walrus"
mvn clean install
cd "${ROOT}"

gamescore="$(find ${ROOT}/walrus/ -name 'games-core*.jar')"
gamesoctc="$(find ${ROOT}/walrus/ -name 'games-octc*.jar')"
nervebukkit="$(find ${ROOT}/walrus/ -name 'nerve-bukkit*.jar')"
ubiquitouisbukkit="$(find ${ROOT}/walrus/ -name 'ubiquitous-bukkit*.jar')"
if [ ! -f "${gamescore}" ] || [ ! -f "${gamesoctc}" ] || [ ! -f "${nervebukkit}" ] || [ ! -f "${ubiquitouisbukkit}" ]; then
	echo "[*] It appears the walrus plugins couldn't be built! Please correct the errors and run this again."
	exit 1
fi

ln -s "${gamescore}" "${ROOT}/plugins/"
ln -s "${nervebukkit}" "${ROOT}/plugins/"
ln -s "${ubiquitouisbukkit}" "${ROOT}/plugins/"

ln -s "${gamesoctc}" "${ROOT}/plugins/GameManager/components/"

# Rerun server for file creation
echo "Re-initializing server instance..."
screen -dmS SportPaper2 bash -c "java -jar '${ROOT}/sportpaper.jar'"
sleep 5
screen -S SportPaper2 -X stuff "stop
"

# Clone and build Walrus Minecraft plugins
EOF
su - walrus <<EOF
echo "Fetching Walrus repositories..."
cd ~
HOME="$(pwd)"
ROOT="${HOME}/devserver"
cd "${ROOT}"
git clone "https://${GUE}:${GPE}@gitlab.com/WalrusNetwork/infrastructure/config"
EOF
su - walrus <<'EOF'
cd ~
HOME="$(pwd)"
ROOT="${HOME}/devserver"
cd "${ROOT}"

cp ${ROOT}/config/* ${ROOT}/ -R

EOF
su - walrus <<EOF
cd ~
HOME="$(pwd)"
ROOT="${HOME}/devserver"
cd "${ROOT}"
git clone "https://${GUE}:${GPE}@gitlab.com/WalrusNetwork/ux-ui/minecraft-ui"
EOF
su - walrus <<'EOF'
cd ~
HOME="$(pwd)"
ROOT="${HOME}/devserver"
cd "${ROOT}"

cp ${ROOT}/minecraft-ui/* ${ROOT}/ -R

git clone "https://github.com/WalrusNetwork/translations"
cp ${ROOT}/translations/* ${ROOT}/ -R

sed -i "s:../translations:${ROOT}/translations:g" "${ROOT}/plugins/Ubiquitous/config.yml"
sed -i "s:../minecraft-ui:${ROOT}/minecraft-ui:g" "${ROOT}/plugins/Ubiquitous/config.yml"

# Configure Maps
echo "Downloading maps..."
EOF
su - walrus <<EOF
echo "Fetching Walrus repositories..."
cd ~
HOME="$(pwd)"
ROOT="${HOME}/devserver"
cd "${ROOT}"
git clone "https://${GUE}:${GPE}@gitlab.com/WalrusNetwork/maps/maps"
EOF
su - walrus <<'EOF'
cd ~
HOME="$(pwd)"
ROOT="${HOME}/devserver"
cd "${ROOT}"

sed -i "s:../maps:${ROOT}/maps:g" "${ROOT}/plugins/GameManager/config.yml"

# TODO: Setup API
echo "[!] You'll have to set up the API yourself. Configure ${ROOT}/plugins/Nerve/config.yml"

echo "Done."
EOF