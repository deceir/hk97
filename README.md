# HK97

HK97 is a Discord bot I built to manage an alliance in Politics & War, an online nation simulation game. It connects Discord commands to the game's APIs and keeps member, banking, and war records in a database.

## What it does

- **Alliance banking:** Tracks member cash and resource balances, matches in-game deposits to accounts, and handles withdrawal requests, loans, repayments, and staff-issued grants.
- **War coordination:** Posts war alerts with military stats and member mentions, finds potential counterattack targets, and manages access to dedicated war room channels.
- **Member audits:** Checks nation activity, military levels, and resource reserves against alliance requirements so staff can follow up with members.
- **Economic tools:** Calculates city, infrastructure, and land costs, estimates city revenue, and appraises a nation's resources using market prices.
- **Membership:** Links Discord users to their in-game nations and provides application forms and interview channels. Recruitment jobs send in-game invitations to new nations.
- **Game data and reports:** Refreshes nation and treasure data, records alliance bank snapshots, and exposes member, bank, and loan records through HTTP endpoints, including a CSV bank export.

## Built with

Java 11, Spring Boot, Maven, Javacord, and Spring Data JPA with MySQL. Game data comes from Politics & War's GraphQL and legacy APIs. Discord slash commands, buttons, and modals provide the main interface; Spring scheduled jobs handle recurring updates and alerts.

## Project status and setup

This is an older project recovered from an incomplete backup. Some support files are still missing, and the full build and runtime have not yet been verified.

Copy [`CONFIG-Template.java`](CONFIG-Template.java) to `src/main/java/net/hk/hk97/Config.java` and fill in the credentials and IDs. The template covers all existing `Config` references. Database settings belong in `application.properties`; both filled-in files are ignored by Git. Some alliance and Discord IDs remain hardcoded and need updating for another server.

---

## 日本語

HK97は、オンライン国家運営ゲーム「Politics & War」の同盟を管理するために開発したDiscordボットです。DiscordのコマンドからゲームのAPIを呼び出し、メンバー情報、銀行取引、戦争の記録をデータベースで管理します。

### 主な機能

- **同盟銀行:** メンバーごとの資金・資源残高を管理し、ゲーム内の入金を口座に反映します。出金申請、融資、返済、運営メンバーによる資金・資源の支給にも対応しています。
- **戦争の連携:** 軍事情報とメンバーへの通知を含む開戦アラートを投稿します。反撃候補の検索や、作戦用チャンネルへのアクセス管理も行います。
- **メンバーの状況確認:** 国家の活動状況、軍備、備蓄資源を同盟の基準と照合し、運営メンバーが対応の必要なメンバーを確認できます。
- **経済計算:** 都市・インフラ・土地の購入費用や都市の収益を計算し、市場価格を使って国家の保有資源を評価します。
- **メンバー管理:** Discordユーザーとゲーム内の国家を紐づけ、加入申請フォームや面談用チャンネルを提供します。新規国家へのゲーム内勧誘メッセージも定期的に送信します。
- **ゲームデータとレポート:** 国家・宝物の情報を更新し、同盟銀行の残高履歴を保存します。メンバー、銀行口座、融資の記録をHTTP APIで取得でき、銀行データはCSVで出力できます。

### 使用技術

Java 11、Spring Boot、Maven、Javacord、Spring Data JPA、MySQLを使用しています。ゲーム情報はPolitics & WarのGraphQL APIと旧APIから取得します。主な操作画面はDiscordのスラッシュコマンド、ボタン、モーダルで、定期更新や通知にはSpringのスケジュール機能を使っています。

### 現在の状態と設定

不完全なバックアップから復元した古いプロジェクトです。一部の補助ファイルが不足しており、プロジェクト全体のビルドと動作はまだ確認できていません。

[`CONFIG-Template.java`](CONFIG-Template.java)を`src/main/java/net/hk/hk97/Config.java`にコピーし、認証情報と各種IDを入力してください。テンプレートには、既存コードで参照されるすべての`Config`フィールドを含めています。データベースの設定は`application.properties`に記述します。設定済みの両ファイルはGitの管理対象外です。同盟やDiscordのIDがコード内に直接書かれている箇所もあるため、別のサーバーで使用する場合は変更が必要です。
