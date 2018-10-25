# 開発の手引き

Tellorcheにおける開発ガイドです

## 着手前にIssueを立てる

もしそのタスクがある程度大きなモノなら、いきなりPull Requestを作らずにまずはIssueを起票しましょう。  
他のメンバーにも問題を可視化することで、アイデアが共有され思わぬ手戻りを防いだりすることができます。

## ブランチを作る

原則として、コミットを直接`master`ブランチへpushすることは禁止です。  
Pull Requestを作ることで変更を可視化すること、コードレビューを実施することでバグを抑制するためです。  
必ず別のブランチを作り、その中で変更をコミットしてください。

このリポジトリへpushする権限を持っているなら、forkせずに直接ブランチを作成して構いません。  
ただし、他のメンバーとのブランチ名重複を防ぐため、以下を参考にしてください。

- 作成するブランチ名は `#{あなたの識別子}/#{一意な識別子}` の形式  
  - たとえば、`shuma/fix-readme-mistake`のような形式です。
- 既に利用したことのあるブランチ名は極力避ける
- 必ず最新の`master`を`pull`し、そこを起点に作る

コマンドに落とし込むと、例えば以下のようになるでしょう:

```sh
pwd
# /Users/shuma/Documents/tellorche
git checkout master
# Already on 'master'
# Your branch is up to date with 'origin/master'.
git pull
# Already up to date
git checkout -b 'shuma/fix-readme-mistake'
# Switched to a new branch 'shuma/fix-readme-mistake'
```
