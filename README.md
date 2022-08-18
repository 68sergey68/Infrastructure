# Развертывание no-managed Kubernetes кластера в облаке.

Внутри каждой компании, ещё с самого начала эксплуатации Kubernetes, развивается собственный подход к развертыванию кластеров. С его помощью единообразно конфигурируется и настраивается окружающая среда и все компоненты инфраструктуры на основе kubernetes. Такой сформированный подход, зачастую, не применим в managed-решение, так как это либо невозможно, либо приведёт к необходимости отключать половину компонентов.
Цель данной работы - изучить существующие способы развертывания no-managed Kubernetes кластера в облаке(YandexCloud).

<div id="toc_container">
<p class="toc_title">Содержание</p>
<ul class="toc_list">
<li><a href="#First_Point_Header">1. Описание стенда.</a>
<li><a href="#Second_Point_Header">2. Развертывание составляющих.</a></li>
<li><a href="#Third_Point_Header">3. Автоматизация создания и настройки кластера Kubernetes.</a></li>
<li><a href="#For_Point_Header">4. Мониторинг.</a></li>
<li><a href="#Five_Point_Header">5. Логирование.</a></li>
</ul>
</div>

<h2 id="First_Point_Header">## 1. Описание стенда.</h2>

Стенд представляет собой кластер k8s, состоящий из 3х Мастеров и 3х Воркеров, а так же сервер CI\CD - TeamCity. В качестве репозитория используется [github](https://github.com/68sergey68/Infrastructure)
Для доступа к ресурсам стенда можно воспользоваться следующими записями в файл hosts: 
```
#OTUS-Project SergeyMarin
51.250.35.133 ip-smarin.sock-shop.ru
51.250.35.133 alertmanager.ip-smarin.ru
51.250.35.133 grafana.ip-smarin.ru
51.250.35.133 kubernetes-dashboard.ip-smarin.ru
51.250.35.133 kibana.ip-smarin.ru
51.250.78.240 teamcity.ip-smarin.ru #Port: 8111
```
<details><summary>Сервер CI\CD - TeamCity </summary>
ВМ развернута в YandexCloud. В качестве агента используется локальный дефолтный агент TeamCity с установленным Docker и pip3 для реализации запуска разных сценариев Ansible в виртуальном python окружении. Доступен из сети Интернет. Гостевой пользователь имеет права на чтение.
Пайплайн хранится в [репозитории](https://github.com/68sergey68/Infrastructure/tree/main/.teamcity) и может меняться как через UI так и через репозиторий(синхронизация на корневом проекте TeamCity)
  
| url | user | password |
|--|--|--|
| http://51.250.78.240:8111/ | guest | guest |
</details>

Кластер Kubernetes

| VM | role | IP(internal) | Zone | vCPU | RAM | HDD | 
|--|--|--|--|--|--|--|
| otus-kubmaster-infra-01 | ControlPlane,etcd | 10.128.0.4 | ru-central1-a | 2 | 4 | 20 | 
| otus-kubmaster-infra-02 | ControlPlane,etcd| 10.129.0.29 | ru-central1-b | 2 | 4 | 20 | 
| otus-kubmaster-infra-03 | ControlPlane,etcd| 10.130.0.23 | ru-central1-c | 2 | 4 | 20 | 
| otus-kubworker-app-01 | APP Worker | 10.130.0.7 | ru-central1-c | 4 | 8 | 30 |  |
| otus-kubworker-infra-01 | Infra Worker | 10.128.0.5 | ru-central1-a | 4 | 8 | 30 | 
| otus-kubworker-infra-02 | Infra Worker | 10.129.0.21 | ru-central1-b | 4 | 8 | 30 | 


|ClusterName| k8s version | Kubespray | Ingress-Nginx LB | CRI | CNI |
|--|--|--|--|--|--|
| k8s-otus | v1.22.12 | 2.19.0 | 51.250.35.133 | containerd://1.6.6 | calico |

[Dashboard](https://kubernetes-dashboard.ip-smarin.ru/#/login)
[Token для авторизации в k8s-dashboard](eyJhbGciOiJSUzI1NiIsImtpZCI6IkRBNW5VWG9VLXdPRERhTXBJczY2MEd2NkZMcWVmUC02aENBdVhlOFRHWGsifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlcm5ldGVzLWRhc2hib2FyZCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJkYXNoYm9hcmQtZ3Vlc3QtdG9rZW4taDQ2NjQiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiZGFzaGJvYXJkLWd1ZXN0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiZGNjZDU5NDUtM2FkYi00MDUyLTgyODgtN2YwOGE4YzE5YzAyIiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50Omt1YmVybmV0ZXMtZGFzaGJvYXJkOmRhc2hib2FyZC1ndWVzdCJ9.cSICfEzg-i5FshzWCMwoJcR50n5ekvJTC2bjf5_K4hBIY4F84O8WuPuswl2KJkmpmUrCl-2ur-P_0jT9l63eVffTGFu1b6-Zp9tmXYTKuRC8WVheO4kbnLBB8JQViz1Vk7YAGDE_bCBN5_T-2bMOHWbWj11SzwiPBuQLlhgm2ZAXQmKd1oRXJmnZ4NcIs77ejkdtXpp1cXLxtMB6wHcrdfvGKlhEBfX0OfYyifKlLmIhBzaMapdM7Q9pncyDFMociSMVV4Hx33YZo-99nxt99uw96fzY7G2uA1WlUd8i3_knZWUs7a9tmd6ZX09bbqrZ9Tauil9CtRVOfn4lX2jtlQ)

<h2 id="Second_Point_Header">## 2. Развертывание составляющих</h2>

<details><summary>2.1 Развертывание сервера CI\CD - TeamCity </summary>

Создаем ВМ в YandexComputeCloud любым удобным способом. Характеристики:

|Имя| TeamCity | 
|--|--|
| OC | Ubuntu |
| RAM | 4Gb |  
| CPU | 2vCPU | 
| IP | 51.250.78.240 (static) | 

На облачную виртуальную машину будем заходить под пользователем ubuntu. Сгенерируем для него ssh-ключи:

`$ ssh-keygen -t rsa -b 4096 -f /home/ubuntu/.ssh/ubuntu-key -q -N ""`
Публичный SSH ключ необходимо указать при создании ВМ.

После старта ВМ необходимо зайти в нее по SSH и настроить сервер TeamCity:

- Для установки свежих пакетов, обновляем их список в репозиториях:

  `apt-get update`
- Для работы Teamcity нужен Java. Вы воспользуемся пакетом openjdk. Для его установки вводим:

  `apt-get install default-jdk`

  Посмотрим используемую версию java:

  `java -version`

  Мы должны увидеть что-то на подобие:

  ```
   openjdk version "11.0.15" 2022-04-19
   OpenJDK Runtime Environment (build 11.0.15+10-Ubuntu-0ubuntu0.20.04.1)
   OpenJDK 64-Bit Server VM (build 11.0.15+10-Ubuntu-0ubuntu0.20.04.1, mixed mode, sharing)
- Для работы Teamcity нужна база данных. Мы воспользуемся MariaDB
  Для ее установки вводим:

  `apt-get install mariadb-server`

  Разрешим автозапуск сервера:

  `systemctl enable mariadb`

  Зададим пароль для суперпользователя:

  `mysqladmin -u root password`

  Подключимся к командной строке SQL:

  `mysql -uroot -p`

  Создаем базу данных для нашего приложения:

  `> CREATE DATABASE teamcity DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;`

  * данной командой мы создадим базу _teamcity_.

  Создадим пользователя для доступа к базе:

  `> GRANT ALL PRIVILEGES ON teamcity.* TO teamcity@localhost IDENTIFIED BY 'teamcity123';`

  * где:

   teamcity.* — разрешает доступ к любой таблице базы teamcity;
   teamcity@localhost — разрешает доступ пользователю teamcity с локального компьютера;
   teamcity123 — пароль для создаваемого пользователя.
   Выходим из командной оболочки SQL:

   `> quit`
- Установка TeamCity

  Создадим каталог, где будет размещаться TeamCity:
  
  `mkdir /opt/teamcity`
  
  Переходим на [страницу загрузки Teamcity](https://www.jetbrains.com/teamcity/download/download-thanks.html?platform=linux) — начнется загрузка файла — прерываем ее. Копируем ссылку на скачивание архива. С помощью нее скачиваем и распаковываем архив:
  ```
  $ wget -O TeamCity.tar.gz https://download.jetbrains.com/teamcity/TeamCity-2020.2.2.tar.gz_ga=2.175301669.60879802.1613028860-1795692882.1612763354
  $ tar zxf TeamCity.tar.gz -C /opt/teamcity --strip-components 1
  ```
  Запустим необходимые сервисы для работы приложения:

  `$ /opt/teamcity/bin/runAll.sh start`

  Пробуем открыть браузер и перейти по ссылке http://<IP-адрес нашего сервера>:8111 — мы должны увидеть стартовую страницу установки Teamcity. В форму для каталога данных вводим путь _/opt/teamcity/.BuildServer_
 
  После кликаем Proceed. Откроется страница выбора используемой СУБД — мы выбираем MySQL
  
  Для работы MySQL необходим дополнительный драйвер — просто нажимаем по Download JDBC driver
  
  Заполняем поля для подключения к базе данных. Нажимаем Proceed — начнется установка и настройка приложения.
 
  Создаем нового пользователя с правами администратора.

- Настройка автозапуска
  
  Создаем пользователя, от которого будет работать наше приложение:

  `useradd teamcity -U -s /bin/false -d /opt/teamcity -m`

  Для каталога с нашим приложением установим владельца — нашего созданного пользователя:

  `chown -R teamcity:teamcity /opt/teamcity`
  
  Создаем юнит в systemd:

  `$ vi /etc/systemd/system/teamcity.service`
   ```
   [Unit]
   Description=Teamcity Service
   After=network.target

   [Service]
   Type=forking

   User=teamcity
   Group=teamcity

   ExecStart=/opt/teamcity/bin/runAll.sh start
   ExecStop=/opt/teamcity/bin/runAll.sh stop

   Restart=on-failure
   RestartSec=10

   [Install]
   WantedBy=multi-user.target
   ```

   Перечитаем конфигурацию systemd:

   `systemctl daemon-reload`

   Можно запустить наш сервис и разрешить его автозапуск:

   ```
    systemctl start teamcity
    systemctl enable teamcity
   ```

   Проверить состояние службы можно командой:

   `systemctl status teamcity`
</details>

<details><summary>2.2 Автоматизация развертывания ВМ в YandexComputeCloud</summary>

Для управления ВМ в YandexCloud используем [кастомный Ansible-модуль](https://github.com/arenadata/ansible-module-yandex-cloud) 

Для авторизации в API YandexCloud требуется [создать сервисный аккаунт](https://cloud.yandex.ru/docs/iam/quickstart-sa)

Создание ВМ сводится к настройке проекта в TeamCity и описанию ВМ в .yaml:
- Настройка проекта TeamCity
 1. Шаг: Подгототовка виртуального окружения ansible, установка зависимостей и скачивание кастомного модуля (работаем в директории ansible)
    ```
    #!/bin/bash
    virtualenv --python=python3 venv
    source venv/bin/activate
    git clone https://github.com/arenadata/ansible-module-yandex-cloud
    cd ./ansible-module-yandex-cloud
    pip3 install -r ../requirements.txt 
    ```
    Содержание файла _requirements.txt_
    ```
    ansible
    jinja2
    yandexcloud
    jsonschema
    ```
  2. Шаг: создание ansible.cfg для корректной работы модуля (работаем в директории ansible/ansible-module-yandex-cloud):

     ```
      cat > ansible.cfg <<EOF 
      %ansible.cfg%
      EOF
     ```
     - где %ansible.cfg% - переменная TeamCity, содержит:
     ```
      [defaults]
      library = ./modules
      module_utils = ./module_utils
     ```
    3. Шаг: Запуск плейбука создания ВМ (работаем в директории ansible/ansible-module-yandex-cloud):
       ```
         #!/bin/bash
         source ../venv/bin/activate
         export ANSIBLE_HOST_KEY_CHECKING=False
         ansible-playbook -i ../inventory.ini ../create-masters.yml --extra-vars 'yc_token="%infra.secrets.yc_token%" ssh_key="%infra.secrets.sshkey%"'
       ```
       - где %infra.secrets.yc_token% и %infra.secrets.sshkey% переменные TeamCity со значением токена аторизации сервисного аккаунта YandexCloud и публичного SSH ключа соответственно.

   - Плейбук создания ВМ
Хранится в репозитории и имеет следующий вид (пример для otus-kubmaster-infra-01)
 
  ```
  - hosts: localhost
    gather_facts: no
    tasks:
      - name: Create vm otus-kubmaster-infra-01
        ycc_vm:
          auth:
            token: "{{ yc_token }}"
          name: otus-kubmaster-infra-01
          login: ubuntu
          public_ssh_key: "{{ ssh_key }}"
          hostname: otus-kubmaster-infra-01
          zone_id: ru-central1-a
          folder_id: b1g8tnleii8cr09vlaa1
          platform_id: "Intel Ice Lake"
          core_fraction: 100
          cores: 2
          memory: 4
          image_id: fd826dalmbcl81eo5nig
          disk_type: hdd
          disk_size: 20
          subnet_id: e9bhbptv4h8qa56sbjlb
          assign_public_ip: true
          preemptible: true
          state: present
  ```
  Значения параметров описаны в [репозитории кастомного модуля](https://github.com/arenadata/ansible-module-yandex-cloud)
</details>

<h2 id="Third_Point_Header">## 3. Автоматизация создания и настройки кластера Kubernetes</h2>
3.1 Создание кластера с помощью Kubespray
- [Скачиваем](https://github.com/kubernetes-sigs/kubespray) необходимую версию Kubespray и кладём в [репозиторий](https://github.com/68sergey68/Infrastructure/tree/main/k8s/kubespray-2.19.0).

- Создаём инвентори файл:
```
all:
  hosts:
    otus-kubmaster-infra-01:
      access_ip: 10.128.0.4
      ansible_host: 10.128.0.4
      ip: 10.128.0.4
    otus-kubmaster-infra-02:
      access_ip: 10.129.0.29
      ansible_host: 10.129.0.29
      ip: 10.129.0.29
    otus-kubmaster-infra-03:
      access_ip: 10.130.0.23
      ansible_host: 10.130.0.23
      ip: 10.130.0.23
    otus-kubworker-infra-01:
      access_ip: 10.128.0.5
      ansible_host: 10.128.0.5
      ip: 10.128.0.5
    otus-kubworker-infra-02:
      access_ip: 10.129.0.21
      ansible_host: 10.129.0.21
      ip: 10.129.0.21
    otus-kubworker-app-01:
      access_ip: 10.130.0.7
      ansible_host: 10.130.0.7
      ip: 10.130.0.7
  children:
    kube-master:
      hosts:
        otus-kubmaster-infra-01:
        otus-kubmaster-infra-02:
        otus-kubmaster-infra-03:
    kube-node:
      hosts:
        otus-kubworker-infra-01:
        otus-kubworker-infra-02:
        otus-kubworker-app-01:
    etcd:
      hosts:
        otus-kubmaster-infra-01:
        otus-kubmaster-infra-02:
        otus-kubmaster-infra-03:
    k8s-cluster:
      children:
        kube-master:
        kube-node:
    calico-rr:
      hosts: {}
```
- Далее производим настройку group_vars.

Для дальнейшего использования CCM(Yandex Cloud Controller Manager) необходимо установить значение параметра "cloud_provider" в group_vars/all/all.yml равным external. Данная настройка добавит флаг --cloud-provider=external во все kubelet-ы. Параметр "cloud_provider" с версии k8s 1.23.0 - deprecated, а с версии 1.24.0 и вовсе - удалён. В связи с этим будем использовать версию v1.22.12(устанавливаем её в group_vars/k8s_cluster/k8s-cluster.yml или передаём в запуске плейбука развертывания.

- Настройка TeamCity для запуска развертывания кластера.
1. Шаг: Подгототовка виртуального окружения ansible, установка зависимостей (работаем в директории kubespray)
    ```
    #!/bin/bash
    virtualenv --python=python3 venv
    source venv/bin/activate
    pip3 install -r ./requirements-2.12.txt    
    ```

2. Шаг: Запуск плейбука cluster.yml (работаем в директории kubespray)
    ```
    #!/bin/bash
    source ./venv/bin/activate
    export ANSIBLE_HOST_KEY_CHECKING=False
    ansible-playbook -b -become-method=sudo -become-user=root -u ubuntu -i inventory/hosts.yml cluster.yml -e 
    kube_version="v1.22.12"   
    ```
После завершения кластер готов к работе:
```
~# kubectl get node
NAME                      STATUS                     ROLES                  AGE     VERSION
otus-kubmaster-infra-01   Ready                      control-plane,master   7m23s   v1.22.12
otus-kubmaster-infra-02   Ready                      control-plane,master   7m23s   v1.22.12
otus-kubmaster-infra-03   Ready                      control-plane,master   7m23s   v1.22.12
otus-kubworker-app-01     Ready                      <none>                 7m23s   v1.22.12
otus-kubworker-infra-01   Ready                      <none>                 7m23s   v1.22.12
otus-kubworker-infra-02   Ready                      <none>                 7m23s   v1.22.12
```
- Установка [Yandex Cloud Controller Manager](https://github.com/deckhouse/yandex-cloud-controller-manager) 
Понадобятся [3 манифеста](https://github.com/68sergey68/Infrastructure/tree/main/k8s/manifests/yandex-ccm), с помощью которых создаются роли RBAC, ServiceAccount, DaemonSet и ключевое, секрет, содержащий в себе данные для авторизации в ЯндексОблаке.
Пример секрета:
```
apiVersion: v1
kind: Secret
metadata:
  labels:
    k8s-app: yandex-cloud-controller-manager
  name: yandex-cloud
  namespace: kube-system
stringData:
  service-account-json: |
    {
       "id": "ajesh3orip69r0vctpf5",
       "service_account_id": "aje3qbblkdf2u2avn4n7",
       "created_at": "2020-01-20T07:43:49Z",
       "key_algorithm": "RSA_2048",
       "public_key": "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAozah4aqf9xkMLRRtNJjz\nZ+xooLV6GLGaHbkl3796r2zWbgm1aNU3xILGeStdTM5XbB651OAfq7YyHoDSiZkj\nBP6W2ZYNO/WjK9N13rWhtFjNiDulLh3LAU47qNy75OsC3SjW58MVHPNriIgB0HLA\nHRE6cguUJtUcKWqOKhoKQVJxXLOhsdjmEEdnHtd9ro3UCcPM7r6fc+MmkCaZgTNl\nkItkDDxodTIqj3Go2EiEyO2DaMye+GqTzSNJOYaHFH4DYhCCgE1/SCY356nER2qH\nymbAGkD2fAp2eGoCEM67AgqrWFEm/yI+FlIvcrn7wC6+NfWUVqPb+N4wuiehCRBO\n8wIDAQAB\n-----END PUBLIC KEY-----\n",
       "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCjNqHhqp/3GQwt\nFG00mPNn7GigtXoYsZoduSXfv3qvbNZuCbVo1TfEgsZ5K11MzldsHrnU4B+rtjIe\ngNKJmSME/pbZlg079aMr03XetaG0WM2IO6UuHcsBTjuo3Lvk6wLdKNbnwxUc82uI\niAHQcsAdETpyC5Qm1Rwpao4qGgpBUnFcs6Gx2OYQR2ce132ujdQJw8zuvp9z4yaQ\nJpmBM2WQi2QMPGh1MiqPcajYSITI7YNozJ74apPNI0k5hocUfgNiEIKATX9IJjfn\nqcRHaofKZsAaQPZ8CnZ4agIQzrsCCqtYUSb/Ij4WUi9yufvALr419ZRWo9v43jC6\nJ6EJEE7zAgMBAAECggEAF0hi3XNesHw1PXUNgxRSnL+fyVU6Hq2vQ5A28+03zjCj\ngj0GUPchpnnVYFGsVJmW5QiZD+INAozSJ4HPBuv+j+bVlCKQrr4C0eyvgt68O6Lz\nZvzDOonrfLsxTYx3jVdtKCl8RsGQkHm1HFvyjk7gUwUzJjO6pbN++fWGZEEkt16W\nFHaGldz2MvZKOwQwj0WUcjF4X8PWTvJ0Ar1i5XoAm35GN+2ziwJKcNt+DsJ3N6MW\ngAkivYE8f44T3fQFg5M1RC6v2Jp2lmtVRxYRI0rcie+HyxJVcHgWTZPdTkwGWKDD\nnRU2OTJoZCJf3BunFtB1P8W+GlmLFdBjTppFhgqI6QKBgQDO61fX49qVRDmORYor\nVWh1tZkw546llwkNqLAe1QoLhqjHGctUs3lOczDqI82PwGKb423JdgKmr9nzrCZW\nq5a/BulHdsGsvkSBGK091fhQYRQnaTF7bnXoyVI8qUerGiV/a8/7W1SM+WIJayZ3\nr5Z9xV9LH/Wy7uWW7Xr2LvP97wKBgQDJ7VuYZVJ17hPIqEaR3P1Jvka+RvusWTPw\n1o6O935tW28Q/S2J661Dv92mTNmll/beyFS1ZkHdQ/1c/92Pr1bM/A4CrQoNDvad\nhd2MnyzVYyHc4p6Yd6VmZisbPpTfa7ZJMzYUN27nj+yPxJyZ/EoLlcXaXcPV3kFo\nZsubNT0DPQKBgQCz/nLmgPWWnMd4ZDOB6IS6yCKfMP6cOtsMP64c0/Mt/ZB5yY1f\ne9PNE1T8h/J71r2wn1DUS8yYlSYB2sFq6U5zk55/pOVq0AQlTIL+5E9iFGCEu/Po\nTDlTKzVXQWXviAoQYoeEPnk5PII0cToAKQS/GV8AqaeAZGHhPWmWF1f1jwKBgHQx\nJ6aejv+bGjk5Uzo1rm3TloOA9uqqfa/U1j0//rjQhy2AccbOHWpBqjo6OHcH5Z82\nKUAkcjvvFoiAFq7KVykm1K0HgyQWeyQTVnPHWBYFsAOZR2c2Wa99lMpdjW6uXTrr\nw++IIkIO2DG2EeKtgLH/4dSQZdLXzE1V8U0DKnOFAoGAQNCBpnE1WHR9H5APr5SF\nuD35dTm3O2OvczlbB0MUhx8R7qPpvLwA5HRSIKAKxobUbGpdgCy6WuncRWg+TjaD\n8zlwZvG2+vtntCFPcIT6ZpGH6k9ppXOPJBxaHZRHJSoilGhF1KvrmY8T5WxTVuyM\nnmypFU40LHcTmvw/a6JY+BM=\n-----END PRIVATE KEY-----\n"
    }
  folder-id: "b1g4c2a3g6vkffp3qacq"
  network-id: "lio12c2a3g6vkff21khj"
```
service-account-json создан согласно [инструкции YandexCloud](https://cloud.yandex.ru/docs/cli/operations/authentication/service-account)

- Применение манифестов Yandex Cloud Controller Manager в кластер средствами TeamCity

1. Шаг: Подготовка cluster config файла для доступа к API 
    ```
    #!/bin/bash
    rm -rf ~/.kube
    mkdir ~/.kube
    echo "%cluster.config%" > ~/.kube/config   
    ```
    - где %cluster.config% - переменная TeamCity, содержащая кластер конфиг с правами, позволяющими работать в неймспейсе kube-system

2. Шаг: Применение  yandex-cloud-controller-manager-secret
    ```
    #!/bin/bash

    echo "%ansible.vault.password%" | tr -d '\r' > vault-password-file.txt
    ansible-vault view yandex-cloud-controller-manager-secret.yaml --vault-password-file vault-password-file.txt > secret.yaml
    kubectl apply -f secret.yaml  
    ```
    - где "%ansible.vault.password%" - пароль для расшифровки манифеста yandex-cloud-controller-manager-secret.yaml
    Для шифрования yandex-cloud-controller-manager-secret.yaml используется [Ansible Vault]( https://docs.ansible.com/ansible/latest/user_guide/vault.html). Ansible Vault шифрует переменные и файлы, тем самым позволяя хранить их в открытом виде. Чтобы использовать Ansible Vault, потребуется один или несколько паролей для шифрования и расшифровки содержимого. 
    
3. Шаг: Применение оставшихся манифестов
    ```
    kubectl apply -f yandex-cloud-controller-manager-rbac.yaml
    kubectl apply -f yandex-cloud-controller-manager.yaml
    ```
4. Шаг: Очистка кластер конфига
    ```
    #!/bin/bash
    rm -rf ~/.kube
    ```
- Установка Ingress-nginx
Скачиваем [helm-chart Ingress-nginx](https://github.com/nginxinc/kubernetes-ingress) 
1. Шаг: Подготовка cluster config файла для доступа к API 
    ```
    #!/bin/bash
    rm -rf ~/.kube
    mkdir ~/.kube
    echo "%cluster.config%" > ~/.kube/config   
    ```
    - где %cluster.config% - переменная TeamCity, содержащая кластер конфиг с правами, позволяющими работать в неймспейсе kube-system

2. Шаг: Установка Ingress-nginx из 
    ```
    helm upgrade --install -f ./values.Infra.yaml ingress-nginx ingress-nginx \
    --repo https://kubernetes.github.io/ingress-nginx \
    --namespace ingress-nginx --create-namespace
    ```
3. Шаг: Очистка кластер конфига
    ```
    #!/bin/bash
    rm -rf 
После установки будет создан DaemonSet и LoadBalancer Service
```
~# kubectl get all -n ingress-nginx
NAME                                 READY   STATUS    RESTARTS     AGE
pod/ingress-nginx-controller-l25d8   1/1     Running   2 (8h ago)   7d23h
pod/ingress-nginx-controller-lkxft   1/1     Running   2 (8h ago)   7d23h
pod/ingress-nginx-controller-qr8z9   1/1     Running   2 (8h ago)   7d23h

NAME                                         TYPE           CLUSTER-IP      EXTERNAL-IP     PORT(S)                      AGE
service/ingress-nginx-controller             LoadBalancer   10.233.36.133   51.250.35.133   80:31364/TCP,443:30364/TCP   7d23h
service/ingress-nginx-controller-admission   ClusterIP      10.233.32.135   <none>          443/TCP                      7d23h

NAME                                      DESIRED   CURRENT   READY   UP-TO-DATE   AVAILABLE   NODE SELECTOR            AGE
daemonset.apps/ingress-nginx-controller   3         3         3       3            3           kubernetes.io/os=linux   7d23h
```

При создании объекта Service с типом LoadBalancer CCM создаёт некий балансировщик, который направит трафик извне к узлам кластера. Конкретно в Yandex.Cloud используется NetworkLoadBalancer и TargetGroup для этих целей. Нашему ingress-nginx lb автоматически присваивается EXTERNAL-IP = 51.250.35.133.

<h2 id="For_Point_Header">## 4. Мониторинг.</h2>

Система мониторинга строится на основе open-source решения [VictoriaMetrics ](https://victoriametrics.github.io/). Данный компонент обеспечивает сбор, обработку, хранение и предоставляет инструменты получения и агрегации метрик. Помимо этого система предоставляет точный механизм оповещений о происшествиях – [Alertmanager](https://prometheus.io/docs/alerting/alertmanager/). VictoriaMetrics имеет богатый набор провайдеров метрик, называемых в терминологии системы экспортерами (exporters). Среди используемых в системе мониторинга можно выделить следующие экспортеры:

* [kube-metrics ](https://github.com/kubernetes/kube-state-metrics)– предоставляет данные о состоянии объектов Kubernetes.
* [node-exporter](https://github.com/prometheus/node_exporter)– предоставляет данные о состоянии Linux хостов

VictoriaMetrics для сбора метрик реализует pull подход.

Кластер VictoriaMetrics состоит из следующих компонентов:

- vmstorage - хранилище данных.
- vminsert - проксирование получаемых данных в vmstorage
- vmselect - выполнение входящих запросов с использованием данных из vmstorage

Каждый сервис можно маштабировать независимо друг от друга и распологать на наиболее подходящем для него оборудовании. Картинка из документации:
![image](https://user-images.githubusercontent.com/80060486/185407550-7ba30a0e-7580-4e05-b038-96e230476057.png)

Помимо этого существуют следующие компоненты:
- vmalert - обработка правил алертинг-а и recording rules с последующей отправкой в AlertManager. Stateless компонент, хранит данные в vmstorage
- vmagent - Компонент, выполняющий опрос экспортеров и отправляющий данный в vmstorage  через vminsert 

## API компонентов системы
### vmagent
- /targets  - UI с краткой информацией по обрабатываемым таргетам
- /api/v1/targets - подробная информация о таргетах, включая все лейблы (может быть полезно для релейблинга)
- /-/reload - reload configuration

### vmalert

- /api/v1/groups - list all loaded groups and rules
- /api/v1/alerts - list all active alerts
- /api/v1/groupID/alertID/status - get alert status by ID
- /metrics - list of application metrics
- /-/reload - reload configuration

Для визуализации метрик используется [Grafana](https://grafana.com/). 
Вся конфигурация мониторинга(включая дашборды Графаны) хранится в [репозитории](https://github.com/68sergey68/Infrastructure/tree/main/k8s/monitoring) и развертывается через [ТимСити](http://51.250.78.240:8111/project.html?projectId=Infrastructure_KubernetesInfra_Monitoring)
![image](https://user-images.githubusercontent.com/80060486/185407964-9cdd9a81-c893-46ec-850d-cd77eb931399.png)


<h2 id="Five_Point_Header">## 5.Логирование</h2>

Общепринятым способом логирования в кубере является отправка логов контейнеров в  std(out|err) с последующим сбором данных логов и отправкой их в систему логирования.

Однако нужно учитывать, что конечно же мы не читаем прямо с std(out|err), докер делает за нас часть работы и обычно складывает логи в ```/var/lib/docker/containers/``` в файлы, которые мы затем и читаем. Чаще всего логи складываются в json.

Самыми распространенными сборщиками являются filebeat и fluent[-bit]. Более быстрым и менее требовательным является fluent-bit. 

## Fluent-bit - описание и развертывание
[Fluent-bit](https://fluentbit.io/) - это:

```
Fluent Bit is an open source and multi-platform Log Processor and Forwarder which allows 
you to collect data/logs from different sources, unify and send them to multiple
 destinations. It's fully compatible with Docker and Kubernetes environments.

Fluent Bit is written in C, have a pluggable architecture supporting around 3
0 extensions. It's fast and lightweight and provide the required security 
for network operations through TLS.
```

Для запуска в кубере используется [helm-чарт](https://github.com/68sergey68/Infrastructure/tree/main/k8s/logging/fluent-bit). Результатом развертывания является DaemonSet, который получаем доступ к логам и выполняет их отправку на сервер логирования.

###Ключевые моменты развертывания:
**Бэкенд**, куда отправляем логи - ElasticSEarch
```yaml
backend:
  type: es
```

**Настройки эластика и индексов**
```yaml
  es:
    host: elasticsearch-service
    port: 9200
    # Elastic Index Name
    index: k8s_cluster
    type: flb_type
    logstash_prefix: k8s_cluster
```
![image](https://user-images.githubusercontent.com/80060486/185407759-acd41c8f-07b7-472a-bea2-8c7e5cbdbffe.png)
